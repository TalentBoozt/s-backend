package com.talentboozt.s_backend.domains.edu.controller;

import jakarta.validation.Valid;
import com.talentboozt.s_backend.domains.edu.dto.evaluation.QuizAttemptRequest;
import com.talentboozt.s_backend.domains.edu.dto.evaluation.QuizRequest;
import com.talentboozt.s_backend.domains.edu.model.EQuizAttempts;
import com.talentboozt.s_backend.domains.edu.model.EQuizzes;
import com.talentboozt.s_backend.domains.edu.service.EduQuizService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/edu/quizzes")
public class EduQuizController {

    private final EduQuizService quizService;

    public EduQuizController(EduQuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping("/course/{courseId}/section/{sectionId}")
    @PreAuthorize("hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE')")
    public ResponseEntity<EQuizzes> createQuiz(
            @PathVariable String courseId,
            @PathVariable String sectionId,
            String creatorId,
            @Valid @RequestBody QuizRequest request) {
        return ResponseEntity.ok(quizService.createQuiz(courseId, sectionId, creatorId, request));
    }

    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<EQuizzes> getQuizByLesson(@PathVariable String lessonId) {
        return ResponseEntity.ok(quizService.getQuizByLessonId(lessonId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EQuizzes> getQuiz(@PathVariable String id) {
        return ResponseEntity.ok(quizService.getQuiz(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE')")
    public ResponseEntity<EQuizzes> updateQuiz(@PathVariable String id, @Valid @RequestBody QuizRequest request) {
        return ResponseEntity.ok(quizService.updateQuiz(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE')")
    public ResponseEntity<Void> deleteQuiz(@PathVariable String id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{quizId}/attempt")
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('ENTERPRISE_INSTRUCTOR')")
    public ResponseEntity<EQuizAttempts> submitAttempt(
            @PathVariable String quizId,
            String userId,
            @Valid @RequestBody QuizAttemptRequest request) {
        return ResponseEntity.ok(quizService.submitQuizAttempt(quizId, userId, request));
    }

    @GetMapping("/{quizId}/attempts/user/{userId}")
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE') or hasAuthority('ENTERPRISE_ADMIN')")
    public ResponseEntity<java.util.List<EQuizAttempts>> getAttempts(
            @PathVariable String quizId,
            @PathVariable String userId) {
        return ResponseEntity.ok(quizService.getAttemptsForUser(quizId, userId));
    }
}
