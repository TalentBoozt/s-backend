package com.talentboozt.s_backend.domains.edu.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talentboozt.s_backend.domains.edu.enums.EAIUsageType;
import com.talentboozt.s_backend.domains.edu.enums.ECourseValidationStatus;
import com.talentboozt.s_backend.domains.edu.enums.ENotificationType;
import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;
import com.talentboozt.s_backend.domains.edu.enums.LLMTaskType;
import com.talentboozt.s_backend.domains.edu.exception.EduAccessDeniedException;
import com.talentboozt.s_backend.domains.edu.exception.EduBadRequestException;
import com.talentboozt.s_backend.domains.edu.exception.EduResourceNotFoundException;
import com.talentboozt.s_backend.domains.edu.model.ECourseSections;
import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.model.ELessons;
import com.talentboozt.s_backend.domains.edu.model.EValidationReports;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ECourseSectionsRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ECoursesRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ELessonsRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EValidationReportsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EduAIValidationService {

    private final EduAICreditService creditService;
    private final EValidationReportsRepository validationRepository;
    private final ECoursesRepository coursesRepository;
    private final ECourseSectionsRepository sectionsRepository;
    private final ELessonsRepository lessonsRepository;
    private final EduNotificationService notificationService;
    private final LLMRouter llmRouter;
    private final EduAccessGuardService accessGuard;
    private final ObjectMapper objectMapper;

    public EduAIValidationService(EduAICreditService creditService,
            EValidationReportsRepository validationRepository,
            ECoursesRepository coursesRepository,
            ECourseSectionsRepository sectionsRepository,
            ELessonsRepository lessonsRepository,
            EduNotificationService notificationService,
            LLMRouter llmRouter,
            EduAccessGuardService accessGuard,
            ObjectMapper objectMapper) {
        this.creditService = creditService;
        this.validationRepository = validationRepository;
        this.coursesRepository = coursesRepository;
        this.sectionsRepository = sectionsRepository;
        this.lessonsRepository = lessonsRepository;
        this.notificationService = notificationService;
        this.llmRouter = llmRouter;
        this.accessGuard = accessGuard;
        this.objectMapper = objectMapper;
    }

    public void submitCourseForValidation(String userId, String courseId) {
        ECourses course = coursesRepository.findById(courseId)
                .orElseThrow(() -> new EduResourceNotFoundException("Course not found"));

        if (!course.getCreatorId().equals(userId)) {
            throw new EduAccessDeniedException("Unauthorized");
        }

        List<ECourseSections> sections = sectionsRepository.findByCourseId(courseId);
        List<ELessons> lessons = lessonsRepository.findByCourseId(courseId);

        if (sections.size() < 3) {
            throw new EduBadRequestException("Course must have at least 3 sections before AI validation.");
        }
        if (lessons.size() < 5) {
            throw new EduBadRequestException("Course must have at least 5 lessons before AI validation.");
        }

        ESubscriptionPlan plan = accessGuard.getUser(userId).getPlan();
        creditService.preValidate(userId, 50, plan);

        EValidationReports lastValidation = getValidationStatus(courseId);
        if (lastValidation != null) {
            Instant lastValidationTime = lastValidation.getCreatedAt();
            boolean hasUpdates = (course.getUpdatedAt() != null && course.getUpdatedAt().isAfter(lastValidationTime));

            if (!hasUpdates) {
                for (ECourseSections s : sections) {
                    if (s.getUpdatedAt() != null && s.getUpdatedAt().isAfter(lastValidationTime)) {
                        hasUpdates = true;
                        break;
                    }
                }
            }
            if (!hasUpdates) {
                for (ELessons l : lessons) {
                    if (l.getUpdatedAt() != null && l.getUpdatedAt().isAfter(lastValidationTime)) {
                        hasUpdates = true;
                        break;
                    }
                }
            }

            if (!hasUpdates) {
                throw new EduBadRequestException(
                        "Validation gaming detected: No content changes were made since your last validation. Please update your content before requesting another review.");
            }
        }

        course.setValidationStatus(ECourseValidationStatus.AI_PENDING);
        coursesRepository.save(course);

        // Run async validator
        runValidationAsync(userId, courseId, course, sections, lessons);
    }

    @Async
    public void runValidationAsync(String userId, String courseId, ECourses course, List<ECourseSections> sections,
            List<ELessons> lessons) {
        try {
            String fullContent = buildCourseContext(course, sections, lessons);

            String systemPrompt = """
                    You are a professional educational quality auditor. Validate the course content based on structure, depth, and clarity.
                    Respond ONLY with a valid JSON object following this exact structure:
                    {
                      "score": 78,
                      "status": "NEEDS_IMPROVEMENT",
                      "summary": "Full summary here",
                      "issues": [
                        { "severity": "HIGH", "area": "content_depth", "message": "Section 2 lacks practical examples" }
                      ],
                      "strengths": ["Well-structured outline", "Clear learning objectives"]
                    }
                    """;

            String userPrompt = "Course Data:\n" + fullContent;

            ESubscriptionPlan plan = accessGuard.getUser(userId).getPlan();
            String aiResponse = llmRouter.generate(plan, LLMTaskType.VALIDATION, systemPrompt, userPrompt, true);

            double score = 0;
            try {
                JsonNode root = objectMapper.readTree(aiResponse);
                score = root.path("score").asDouble();
            } catch (Exception e) {
                log.error("Failed to parse AI score: {}", e.getMessage());
            }

            ECourseValidationStatus aiStatus;
            if (score >= 85)
                aiStatus = ECourseValidationStatus.MANUAL_PENDING;
            else if (score >= 60)
                aiStatus = ECourseValidationStatus.NEEDS_IMPROVEMENT;
            else
                aiStatus = ECourseValidationStatus.AI_REJECTED;

            int tokenCost = 50;
            creditService.deductCredits(userId, courseId, tokenCost, EAIUsageType.COURSE_VALIDATION,
                    "Full professional course validation ruleset scan...", aiResponse);

            // Fetch latest course in case of parallel updates
            ECourses latestCourse = coursesRepository.findById(courseId).orElse(course);
            latestCourse.setValidationStatus(aiStatus);
            latestCourse.setAiScore(score);
            coursesRepository.save(latestCourse);

            EValidationReports report = EValidationReports.builder()
                    .courseId(courseId)
                    .userId(latestCourse.getCreatorId())
                    .reviewerId("AI_SYSTEM")
                    .status(aiStatus.name())
                    .aiScore(score)
                    .feedback(aiResponse)
                    .createdBy("AI_SYSTEM")
                    .createdAt(Instant.now())
                    .build();

            validationRepository.save(report);

            notificationService.triggerNotification(
                    latestCourse.getCreatorId(),
                    "AI Validation Complete",
                    "Your course validation is finished. Result: " + aiStatus.name(),
                    ENotificationType.COURSE_UPDATE,
                    courseId);

        } catch (Exception e) {
            log.error("Validation failed for course {}: {}", courseId, e.getMessage());
            ECourses failedCourse = coursesRepository.findById(courseId).orElse(course);
            failedCourse.setValidationStatus(ECourseValidationStatus.NEEDS_IMPROVEMENT);
            coursesRepository.save(failedCourse);

            notificationService.triggerNotification(
                    userId,
                    "AI Validation Failed",
                    "There was an error parsing the course validation. Please try again or contact support.",
                    ENotificationType.SYSTEM_ALERT,
                    courseId);
        }
    }

    public EValidationReports getValidationStatus(String courseId) {
        return validationRepository.findFirstByCourseIdOrderByCreatedAtDesc(courseId).orElse(null);
    }

    private String buildCourseContext(ECourses course, List<ECourseSections> sections, List<ELessons> lessons) {
        StringBuilder builder = new StringBuilder();
        builder.append("TITLE: ").append(course.getTitle()).append("\n");
        builder.append("DESCRIPTION: ").append(course.getDescription()).append("\n\n");

        for (ECourseSections section : sections) {
            builder.append("## SECTION: ").append(section.getTitle()).append("\n");
            List<ELessons> sectionLessons = lessons.stream()
                    .filter(l -> section.getId().equals(l.getSectionId()))
                    .collect(Collectors.toList());

            for (ELessons lesson : sectionLessons) {
                builder.append("### LESSON: ").append(lesson.getTitle()).append("\n");
                builder.append("CONTENT: ").append(lesson.getTextContent()).append("\n");
            }
        }
        return builder.toString();
    }
}
