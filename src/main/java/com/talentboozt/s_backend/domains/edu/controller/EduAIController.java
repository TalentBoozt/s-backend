package com.talentboozt.s_backend.domains.edu.controller;

import jakarta.validation.Valid;
import com.talentboozt.s_backend.domains.edu.dto.ai.AIGenerationRequest;
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
import java.util.List;

import java.util.Map;

@RestController
@RequestMapping("/api/edu/ai")
public class EduAIController {

    private final EduAIEngineService engineService;
    private final EduAICreditService creditService;
    private final EduAIValidationService validationService;
    private final RateLimiterService rateLimiterService;

    public EduAIController(EduAIEngineService engineService, EduAICreditService creditService, EduAIValidationService validationService, RateLimiterService rateLimiterService) {
        this.engineService = engineService;
        this.creditService = creditService;
        this.validationService = validationService;
        this.rateLimiterService = rateLimiterService;
    }

    @GetMapping("/credits")
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<EAiCredits> getAICredits(@AuthenticatedUser String userId) {
        return ResponseEntity.ok(creditService.getUserCredits(userId));
    }

    @GetMapping("/credits/ledger")
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<List<ECreditLedger>> getCreditLedger(@AuthenticatedUser String userId) {
        return ResponseEntity.ok(creditService.getCreditLedger(userId));
    }

    @PostMapping("/generate-outline/{courseId}")
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<Map<String, String>> generateOutline(
            @PathVariable String courseId,
            @AuthenticatedUser String userId,
            @Valid @RequestBody AIGenerationRequest request) {
        if (!rateLimiterService.checkRateLimit(userId, "edu-ai-generate")) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests");
        }
        String response = engineService.generateCourseOutline(userId, courseId, request);
        return ResponseEntity.ok(Map.of("data", response));
    }

    @PostMapping("/generate-lesson/{courseId}")
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<Map<String, String>> generateLesson(
            @PathVariable String courseId,
            @AuthenticatedUser String userId,
            @RequestParam String lessonObjective) {
        if (!rateLimiterService.checkRateLimit(userId, "edu-ai-generate")) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests");
        }
        String response = engineService.generateLessonContent(userId, courseId, lessonObjective);
        return ResponseEntity.ok(Map.of("data", response));
    }

    @PostMapping("/generate-quiz/{courseId}")
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<Map<String, String>> generateQuiz(
            @PathVariable String courseId,
            @AuthenticatedUser String userId,
            @RequestParam String topic) {
        if (!rateLimiterService.checkRateLimit(userId, "edu-ai-generate")) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests");
        }
        String response = engineService.generateSystemQuiz(userId, courseId, topic);
        return ResponseEntity.ok(Map.of("data", response));
    }

    @PostMapping("/validate/{courseId}")
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('SELLER_FREE') or hasAuthority('SELLER_PRO') or hasAuthority('SELLER_PREMIUM')")
    public ResponseEntity<?> validateCourse(
            @PathVariable String courseId,
            @AuthenticatedUser String userId) {
        validationService.submitCourseForValidation(userId, courseId);
        return ResponseEntity.accepted().body(Map.of("message", "Course validation started successfully", "status", "AI_PENDING"));
    }

    @GetMapping("/validate/{courseId}/status")
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('SELLER_FREE') or hasAuthority('SELLER_PRO') or hasAuthority('SELLER_PREMIUM')")
    public ResponseEntity<?> getValidationStatus(
            @PathVariable String courseId) {
        return ResponseEntity.ok(validationService.getValidationStatus(courseId));
    }
}
