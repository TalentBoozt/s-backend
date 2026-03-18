package com.talentboozt.s_backend.domains.edu.controller;

import com.talentboozt.s_backend.domains.edu.dto.ai.AIGenerationRequest;
import com.talentboozt.s_backend.domains.edu.model.EAiCredits;
import com.talentboozt.s_backend.domains.edu.service.EduAICreditService;
import com.talentboozt.s_backend.domains.edu.service.EduAIEngineService;
import com.talentboozt.s_backend.domains.edu.service.EduAIValidationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/edu/ai")
public class EduAIController {

    private final EduAIEngineService engineService;
    private final EduAICreditService creditService;
    private final EduAIValidationService validationService;

    public EduAIController(EduAIEngineService engineService, EduAICreditService creditService, EduAIValidationService validationService) {
        this.engineService = engineService;
        this.creditService = creditService;
        this.validationService = validationService;
    }

    @GetMapping("/credits/{userId}")
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<EAiCredits> getAICredits(@PathVariable String userId) {
        return ResponseEntity.ok(creditService.getUserCredits(userId));
    }

    @PostMapping("/generate-outline/{courseId}")
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<Map<String, String>> generateOutline(
            @PathVariable String courseId,
            @RequestParam String userId,
            @RequestBody AIGenerationRequest request) {
        String response = engineService.generateCourseOutline(userId, courseId, request);
        return ResponseEntity.ok(Map.of("data", response));
    }

    @PostMapping("/generate-lesson/{courseId}")
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<Map<String, String>> generateLesson(
            @PathVariable String courseId,
            @RequestParam String userId,
            @RequestParam String lessonObjective) {
        String response = engineService.generateLessonContent(userId, courseId, lessonObjective);
        return ResponseEntity.ok(Map.of("data", response));
    }

    @PostMapping("/generate-quiz/{courseId}")
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<Map<String, String>> generateQuiz(
            @PathVariable String courseId,
            @RequestParam String userId,
            @RequestParam String topic) {
        String response = engineService.generateSystemQuiz(userId, courseId, topic);
        return ResponseEntity.ok(Map.of("data", response));
    }

    @PostMapping("/validate/{courseId}")
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<?> validateCourse(
            @PathVariable String courseId,
            @RequestParam String userId) {
        return ResponseEntity.ok(validationService.validateCourseContent(userId, courseId));
    }
}
