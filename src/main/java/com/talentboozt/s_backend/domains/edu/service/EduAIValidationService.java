package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.enums.EAIUsageType;
import com.talentboozt.s_backend.domains.edu.enums.ECourseValidationStatus;
import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.model.EValidationReports;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ECoursesRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EValidationReportsRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class EduAIValidationService {

    private final EduAICreditService creditService;
    private final EValidationReportsRepository validationRepository;
    private final ECoursesRepository coursesRepository;

    public EduAIValidationService(EduAICreditService creditService,
            EValidationReportsRepository validationRepository,
            ECoursesRepository coursesRepository) {
        this.creditService = creditService;
        this.validationRepository = validationRepository;
        this.coursesRepository = coursesRepository;
    }

    public EValidationReports validateCourseContent(String userId, String courseId) {
        ECourses course = coursesRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        int tokenCost = 50; // Validation scan is a heavy operation
        String mockupLLMReport = "LLM Scan Complete. Score: 85/100. Structure is clean, spelling is perfect, but lacks deep multimedia references.";

        creditService.deductCredits(userId, courseId, tokenCost, EAIUsageType.COURSE_VALIDATION,
                "Execute full course validation ruleset scan...", mockupLLMReport);

        // Update root Course markers
        course.setValidationStatus(ECourseValidationStatus.AI_APPROVED);
        course.setAiScore(85.0);
        coursesRepository.save(course);

        // Create the Validation Object for tracking in Dashboard views
        EValidationReports report = EValidationReports.builder()
                .courseId(courseId)
                .userId(course.getCreatorId()) // creator logic
                .reviewerId(userId) // The AI requested on behalf of user
                .status(ECourseValidationStatus.AI_APPROVED.name())
                .aiScore(85.0)
                .createdBy("AI_SYSTEM")
                .createdAt(Instant.now())
                .build();

        return validationRepository.save(report);
    }
}
