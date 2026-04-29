package com.talentboozt.s_backend.domains.edu.controller;

import com.talentboozt.s_backend.domains.edu.service.*;
import jakarta.validation.Valid;
import com.talentboozt.s_backend.domains.edu.dto.ai.AIGenerationRequest;
import com.talentboozt.s_backend.domains.edu.exception.EduBadRequestException;
import com.talentboozt.s_backend.domains.edu.model.EAiCredits;
import com.talentboozt.s_backend.domains.edu.service.EduAICreditService;
import com.talentboozt.s_backend.domains.edu.service.EduAIEngineService;
import com.talentboozt.s_backend.domains.edu.service.EduAIValidationService;
import com.talentboozt.s_backend.shared.security.service.RateLimiterService;
import com.talentboozt.s_backend.shared.security.annotations.AuthenticatedUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.talentboozt.s_backend.domains.edu.model.ECreditLedger;
import com.talentboozt.s_backend.domains.edu.service.EduAccessGuardService;
import java.util.List;

import java.util.Map;

@RestController
@RequestMapping("/api/edu/ai")
public class EduAIController {

    private final EduAIEngineService engineService;
    private final EduAICreditService creditService;
    private final EduAIValidationService validationService;
    private final EduContentValidationService contentValidationService;
    private final RateLimiterService rateLimiterService;
    private final EduAccessGuardService accessGuard;

    public EduAIController(EduAIEngineService engineService, EduAICreditService creditService,
            EduAIValidationService validationService, EduContentValidationService contentValidationService,
            RateLimiterService rateLimiterService, EduAccessGuardService accessGuard) {
        this.engineService = engineService;
        this.creditService = creditService;
        this.validationService = validationService;
        this.contentValidationService = contentValidationService;
        this.rateLimiterService = rateLimiterService;
        this.accessGuard = accessGuard;
    }

    @GetMapping("/credits")
    @PreAuthorize("hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE') or hasAuthority('SELLER_PRO') or hasAuthority('SELLER_PREMIUM')")
    public ResponseEntity<EAiCredits> getAICredits(@AuthenticatedUser String userId) {
        return ResponseEntity.ok(creditService.getUserCredits(userId));
    }

    @GetMapping("/credits/ledger")
    @PreAuthorize("hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE') or hasAuthority('SELLER_PRO') or hasAuthority('SELLER_PREMIUM')")
    public ResponseEntity<List<ECreditLedger>> getCreditLedger(@AuthenticatedUser String userId) {
        return ResponseEntity.ok(creditService.getCreditLedger(userId));
    }

    @PostMapping("/generate-outline/{courseId}")
    @PreAuthorize("hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE') or hasAuthority('SELLER_PRO') or hasAuthority('SELLER_PREMIUM')")
    public ResponseEntity<Map<String, String>> generateOutline(
            @PathVariable String courseId,
            @AuthenticatedUser String userId,
            @Valid @RequestBody AIGenerationRequest request) {
        if (!rateLimiterService.checkRateLimit(userId, "edu-ai-generate")) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests");
        }
        accessGuard.enforceFeatureAccess(userId, "AI_GENERATION");
        accessGuard.enforceAIGenerationLimits(userId, request.getTopic());
        accessGuard.enforceCourseOwnership(userId, courseId);

        String response = engineService.generateCourseOutline(userId, courseId, request);
        return ResponseEntity.ok(Map.of("data", response));
    }

    @PostMapping("/generate-lesson/{courseId}")
    @PreAuthorize("hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE') or hasAuthority('SELLER_PRO') or hasAuthority('SELLER_PREMIUM')")
    public ResponseEntity<Map<String, String>> generateLesson(
            @PathVariable String courseId,
            @AuthenticatedUser String userId,
            @RequestParam String lessonObjective) {
        if (!rateLimiterService.checkRateLimit(userId, "edu-ai-generate")) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests");
        }
        accessGuard.enforceFeatureAccess(userId, "AI_GENERATION");
        accessGuard.enforceAIGenerationLimits(userId, lessonObjective);
        accessGuard.enforceCourseOwnership(userId, courseId);

        String response = engineService.generateLessonContent(userId, courseId, lessonObjective);
        return ResponseEntity.ok(Map.of("data", response));
    }

    @PostMapping("/generate-quiz/{courseId}")
    @PreAuthorize("hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE') or hasAuthority('SELLER_PRO') or hasAuthority('SELLER_PREMIUM')")
    public ResponseEntity<Map<String, String>> generateQuiz(
            @PathVariable String courseId,
            @AuthenticatedUser String userId,
            @RequestParam String topic) {
        if (!rateLimiterService.checkRateLimit(userId, "edu-ai-generate")) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests");
        }
        accessGuard.enforceFeatureAccess(userId, "AI_GENERATION");
        accessGuard.enforceAIGenerationLimits(userId, topic);
        accessGuard.enforceCourseOwnership(userId, courseId);

        String response = engineService.generateSystemQuiz(userId, courseId, topic);
        return ResponseEntity.ok(Map.of("data", response));
    }

    @PostMapping("/generate-summary/{courseId}")
    @PreAuthorize("hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE') or hasAuthority('SELLER_PRO') or hasAuthority('SELLER_PREMIUM')")
    public ResponseEntity<Map<String, String>> generateSummary(
            @PathVariable String courseId,
            @AuthenticatedUser String userId,
            @RequestBody Map<String, String> body) {
        String courseContext = body.get("courseContext");
        if (!rateLimiterService.checkRateLimit(userId, "edu-ai-generate")) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests");
        }
        accessGuard.enforceFeatureAccess(userId, "AI_GENERATION");
        accessGuard.enforceAIGenerationLimits(userId, courseContext);
        accessGuard.enforceCourseOwnership(userId, courseId);

        String response = engineService.generateCourseSummary(userId, courseId, courseContext);
        return ResponseEntity.ok(Map.of("data", response));
    }

    @PostMapping("/translate/{courseId}")
    @PreAuthorize("hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE') or hasAuthority('SELLER_PRO') or hasAuthority('SELLER_PREMIUM')")
    public ResponseEntity<Map<String, String>> translateContent(
            @PathVariable String courseId,
            @AuthenticatedUser String userId,
            @RequestBody Map<String, String> body) {
        String content = body.get("content");
        String language = body.get("language");
        if (!rateLimiterService.checkRateLimit(userId, "edu-ai-generate")) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests");
        }
        accessGuard.enforceFeatureAccess(userId, "AI_GENERATION");
        accessGuard.enforceAIGenerationLimits(userId, content);
        accessGuard.enforceCourseOwnership(userId, courseId);

        String response = engineService.translateCourseContent(userId, courseId, content, language);
        return ResponseEntity.ok(Map.of("data", response));
    }

    @PostMapping("/rewrite/{courseId}")
    @PreAuthorize("hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE') or hasAuthority('SELLER_PRO') or hasAuthority('SELLER_PREMIUM')")
    public ResponseEntity<Map<String, String>> rewriteContent(
            @PathVariable String courseId,
            @AuthenticatedUser String userId,
            @RequestBody Map<String, String> body) {
        String content = body.get("content");
        String style = body.get("style");
        if (!rateLimiterService.checkRateLimit(userId, "edu-ai-generate")) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests");
        }
        accessGuard.enforceFeatureAccess(userId, "AI_GENERATION");
        accessGuard.enforceAIGenerationLimits(userId, content);
        accessGuard.enforceCourseOwnership(userId, courseId);

        String response = engineService.rewriteContent(userId, courseId, content, style);
        return ResponseEntity.ok(Map.of("data", response));
    }

    @PostMapping("/revise/{courseId}")
    @PreAuthorize("hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE') or hasAuthority('SELLER_PRO') or hasAuthority('SELLER_PREMIUM')")
    public ResponseEntity<Map<String, String>> reviseContent(
            @PathVariable String courseId,
            @AuthenticatedUser String userId,
            @RequestBody Map<String, String> body) {
        String content = body.get("content");
        if (!rateLimiterService.checkRateLimit(userId, "edu-ai-generate")) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests");
        }
        accessGuard.enforceFeatureAccess(userId, "AI_GENERATION");
        accessGuard.enforceAIGenerationLimits(userId, content);
        accessGuard.enforceCourseOwnership(userId, courseId);

        String response = engineService.reviseContent(userId, courseId, content);
        return ResponseEntity.ok(Map.of("data", response));
    }

    @PostMapping("/validate/{courseId}")
    @PreAuthorize("hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE') or hasAuthority('SELLER_PRO') or hasAuthority('SELLER_PREMIUM')")
    public ResponseEntity<?> validateCourse(
            @PathVariable String courseId,
            @AuthenticatedUser String userId) {
        accessGuard.enforceFeatureAccess(userId, "COURSE_VALIDATION");
        accessGuard.enforceCourseOwnership(userId, courseId);

        validationService.submitCourseForValidation(userId, courseId);
        return ResponseEntity.accepted()
                .body(Map.of("message", "Course validation started successfully", "status", "AI_PENDING"));
    }

    @GetMapping("/validate/{courseId}/status")
    @PreAuthorize("hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE') or hasAuthority('SELLER_PRO') or hasAuthority('SELLER_PREMIUM')")
    public ResponseEntity<?> getValidationStatus(
            @PathVariable String courseId,
            @AuthenticatedUser String userId) {
        accessGuard.enforceFeatureAccess(userId, "COURSE_VALIDATION");
        accessGuard.enforceCourseOwnership(userId, courseId);

        return ResponseEntity.ok(validationService.getValidationStatus(courseId));
    }

    @PostMapping("/check-content")
    @PreAuthorize("hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_PRO') or hasAuthority('SELLER_PREMIUM')")
    public ResponseEntity<?> validateContent(
            @AuthenticatedUser String userId,
            @RequestBody Map<String, String> body) {
        String content = body.get("content");
        if (content == null)
            throw new EduBadRequestException("Content is required");

        // Deduction and access checks (reduced cost for single snippet check)
        accessGuard.enforceFeatureAccess(userId, "AI_GENERATION");

        return ResponseEntity.ok(contentValidationService.validateContent(content));
    }
}
