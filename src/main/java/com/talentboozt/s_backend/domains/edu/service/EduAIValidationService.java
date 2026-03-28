package com.talentboozt.s_backend.domains.edu.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talentboozt.s_backend.domains.edu.enums.EAIUsageType;
import com.talentboozt.s_backend.domains.edu.enums.ECourseValidationStatus;
import com.talentboozt.s_backend.domains.edu.model.ECourseSections;
import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.model.ELessons;
import com.talentboozt.s_backend.domains.edu.model.EValidationReports;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ECourseSectionsRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ECoursesRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ELessonsRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EValidationReportsRepository;
import lombok.extern.slf4j.Slf4j;
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
    private final LLMClient llmClient;
    private final ObjectMapper objectMapper;

    public EduAIValidationService(EduAICreditService creditService,
                                  EValidationReportsRepository validationRepository,
                                  ECoursesRepository coursesRepository,
                                  ECourseSectionsRepository sectionsRepository,
                                  ELessonsRepository lessonsRepository,
                                  LLMClient llmClient,
                                  ObjectMapper objectMapper) {
        this.creditService = creditService;
        this.validationRepository = validationRepository;
        this.coursesRepository = coursesRepository;
        this.sectionsRepository = sectionsRepository;
        this.lessonsRepository = lessonsRepository;
        this.llmClient = llmClient;
        this.objectMapper = objectMapper;
    }

    public EValidationReports validateCourseContent(String userId, String courseId) {
        ECourses course = coursesRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        List<ECourseSections> sections = sectionsRepository.findByCourseId(courseId);
        List<ELessons> lessons = lessonsRepository.findByCourseId(courseId);

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

        String aiResponse = llmClient.generate(systemPrompt, userPrompt, true);

        double score = 0;
        try {
            JsonNode root = objectMapper.readTree(aiResponse);
            score = root.path("score").asDouble();
        } catch (Exception e) {
            log.error("Failed to parse AI score: {}", e.getMessage());
        }

        ECourseValidationStatus aiStatus;
        if (score >= 85) aiStatus = ECourseValidationStatus.AI_APPROVED;
        else if (score >= 60) aiStatus = ECourseValidationStatus.NEEDS_IMPROVEMENT;
        else aiStatus = ECourseValidationStatus.AI_REJECTED;

        int tokenCost = 50; 
        creditService.deductCredits(userId, courseId, tokenCost, EAIUsageType.COURSE_VALIDATION,
                "Full professional course validation ruleset scan...", aiResponse);

        // Update root Course markers
        course.setValidationStatus(aiStatus);
        course.setAiScore(score);
        coursesRepository.save(course);

        // Create the Validation Object for tracking in Dashboard views
        EValidationReports report = EValidationReports.builder()
                .courseId(courseId)
                .userId(course.getCreatorId())
                .reviewerId(userId)
                .status(aiStatus.name())
                .aiScore(score)
                .feedback(aiResponse)
                .createdBy("AI_SYSTEM")
                .createdAt(Instant.now())
                .build();

        return validationRepository.save(report);
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
