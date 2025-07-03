package com.talentboozt.s_backend.Service.PLAT_COURSES;

import com.talentboozt.s_backend.DTO.COM_COURSES.QuizDTO;
import com.talentboozt.s_backend.DTO.PLAT_COURSES.LeaderboardEntry;
import com.talentboozt.s_backend.DTO.PLAT_COURSES.QuestionAnswer;
import com.talentboozt.s_backend.Model.PLAT_COURSES.QuizAttempt;
import com.talentboozt.s_backend.Repository.PLAT_COURSES.QuizAttemptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class QuizSubmissionService {

    @Autowired
    private QuizAttemptRepository attemptRepo;

    // Submit or retry quiz
    public QuizAttempt submitQuiz(String empId, String courseId, String moduleId, QuizDTO quiz, List<QuestionAnswer> answers) {
        List<QuizAttempt> past = attemptRepo.findByEmployeeIdAndQuizId(empId, quiz.getId());

        if (past.size() >= quiz.getAttemptLimit()) {
            throw new RuntimeException("Max attempts reached");
        }

        int correct = (int) quiz.getQuestions().stream()
                .filter(q -> {
                    List<String> sel = answers.stream()
                            .filter(a -> a.getQuestionId().equals(q.getId()))
                            .findFirst()
                            .map(QuestionAnswer::getSelectedAnswers)
                            .orElse(List.of());
                    return new HashSet<>(q.getCorrectAnswer()).equals(new HashSet<>(sel));
                })
                .count();

        double score = 100.0 * correct / quiz.getQuestions().size();

        QuizAttempt attempt = new QuizAttempt();
        attempt.setEmployeeId(empId);
        attempt.setCourseId(courseId);
        attempt.setModuleId(moduleId);
        attempt.setQuizId(quiz.getId());
        attempt.setAttemptNumber(past.size() + 1);
        attempt.setAnswers(answers);
        attempt.setCorrectCount(correct);
        attempt.setTotalQuestions(quiz.getQuestions().size());
        attempt.setScore(score);
        attempt.setSubmittedAt(Instant.now().toString());

        return attemptRepo.save(attempt);
    }

    // Fetch previous attempts
    public List<QuizAttempt> getAttempts(String empId, String quizId) {
        return attemptRepo.findByEmployeeIdAndQuizId(empId, quizId);
    }

    // Build leaderboard top N
    public List<LeaderboardEntry> getLeaderboard(String quizId, int topN) {
        List<QuizAttempt> all = attemptRepo.findByQuizIdOrderByScoreDesc(quizId);
        Map<String, LeaderboardEntry> map = new LinkedHashMap<>();

        for (QuizAttempt a : all) {
            map.compute(a.getEmployeeId(), (k, existing) -> {
                if (existing == null || a.getScore() > existing.getBestScore()) {
                    return new LeaderboardEntry(a.getEmployeeId(), a.getScore(), a.getSubmittedAt());
                }
                return existing;
            });
            if (map.size() >= topN) break;
        }
        return new ArrayList<>(map.values());
    }
}
