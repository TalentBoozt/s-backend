package com.talentboozt.s_backend.Controller.PLAT_COURSES;

import com.talentboozt.s_backend.DTO.PLAT_COURSES.LeaderboardEntry;
import com.talentboozt.s_backend.DTO.PLAT_COURSES.QuizSubmissionRequest;
import com.talentboozt.s_backend.Repository.COM_COURSES.CourseRepository;
import com.talentboozt.s_backend.Service.PLAT_COURSES.QuizSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/course/quiz")
public class QuizSubmissionController {

    @Autowired
    private QuizSubmissionService service;

    @Autowired
    private CourseRepository courseRepo;

    @PostMapping("/submit")
    public ResponseEntity<?> submit(@RequestBody QuizSubmissionRequest req) {
        var course = courseRepo.findById(req.getCourseId())
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

    @GetMapping("/leaderboard")
    public ResponseEntity<List<LeaderboardEntry>> getLeaderboard(
            @RequestParam String quizId, @RequestParam(defaultValue = "10") int top) {
        return ResponseEntity.ok(service.getLeaderboard(quizId, top));
    }
}
