package com.talentboozt.s_backend.domains.plat_courses.controller;

import com.talentboozt.s_backend.domains.plat_courses.dto.LeaderboardEntry;
import com.talentboozt.s_backend.domains.plat_courses.dto.QuizSubmissionRequest;
import com.talentboozt.s_backend.domains.com_courses.repository.CourseRepository;
import com.talentboozt.s_backend.domains.plat_courses.service.QuizSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/v2/course/quiz")
public class QuizSubmissionController {

    @Autowired
    private QuizSubmissionService service;

    @Autowired
    private CourseRepository courseRepo;

    @PostMapping("/submit")
    public ResponseEntity<?> submit(@RequestBody QuizSubmissionRequest req) {
        var course = courseRepo.findById(Objects.requireNonNull(req.getCourseId()))
                .orElseThrow(() -> new RuntimeException("Course not found"));
        var quiz = course.getQuizzes().stream()
                .filter(q -> q.getId().equals(req.getQuizId())).findFirst()
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        var attempt = service.submitQuiz(
                req.getEmployeeId(),
                req.getCourseId(),
                req.getModuleId(),
                quiz,
                req.getAnswers()
        );
        return ResponseEntity.ok(Map.of(
                "score", attempt.getScore(),
                "correct", attempt.getCorrectCount(),
                "total", attempt.getTotalQuestions(),
                "attemptNumber", attempt.getAttemptNumber()
        ));
    }

    @GetMapping("/attempts/{employeeId}/{quizId}")
    public ResponseEntity<?> getAttempts(
            @PathVariable String employeeId,
            @PathVariable String quizId
    ) {
        return ResponseEntity.ok(service.getAttempts(employeeId, quizId));
    }

    @GetMapping("/leaderboard/quiz/{quizId}")
    public ResponseEntity<List<LeaderboardEntry>> getQuizLeaderboard(@PathVariable String quizId,
                                                                     @RequestParam(defaultValue = "10") int topN) {
        return ResponseEntity.ok(service.getLeaderboard(quizId, topN));
    }

    @GetMapping("/leaderboard/course/{courseId}")
    public ResponseEntity<List<LeaderboardEntry>> getCourseLeaderboard(@PathVariable String courseId,
                                                                       @RequestParam(defaultValue = "10") int topN) {
        return ResponseEntity.ok(service.getCourseLeaderboard(courseId, topN));
    }
}
