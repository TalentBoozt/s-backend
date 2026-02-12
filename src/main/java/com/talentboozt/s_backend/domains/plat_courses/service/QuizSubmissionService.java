package com.talentboozt.s_backend.domains.plat_courses.service;

import com.talentboozt.s_backend.domains.com_courses.dto.QuizDTO;
import com.talentboozt.s_backend.domains.plat_courses.dto.LeaderboardEntry;
import com.talentboozt.s_backend.domains.plat_courses.dto.QuestionAnswer;
import com.talentboozt.s_backend.domains.plat_courses.model.QuizAttempt;
import com.talentboozt.s_backend.domains.plat_courses.repository.mongodb.QuizAttemptRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

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

    public List<LeaderboardEntry> getCourseLeaderboard(String courseId, int topN) {
        List<QuizAttempt> attempts = attemptRepo.findByCourseId(courseId);
        Map<String, List<QuizAttempt>> groupedByEmployee = new HashMap<>();

        // Group attempts by employee and keep only best per quiz
        for (QuizAttempt attempt : attempts) {
            groupedByEmployee.computeIfAbsent(attempt.getEmployeeId(), k -> new ArrayList<>()).add(attempt);
        }

        List<LeaderboardEntry> leaderboard = new ArrayList<>();

        for (Map.Entry<String, List<QuizAttempt>> entry : groupedByEmployee.entrySet()) {
            String employeeId = entry.getKey();
            Map<String, QuizAttempt> bestAttemptsPerQuiz = new HashMap<>();

            for (QuizAttempt a : entry.getValue()) {
                bestAttemptsPerQuiz.compute(a.getQuizId(), (quizId, existing) -> {
                    if (existing == null || a.getScore() > existing.getScore()) {
                        return a;
                    }
                    return existing;
                });
            }

            double totalScore = bestAttemptsPerQuiz.values().stream()
                    .mapToDouble(QuizAttempt::getScore)
                    .sum();

            String latestSubmission = bestAttemptsPerQuiz.values().stream()
                    .map(QuizAttempt::getSubmittedAt)
                    .max(String::compareTo) // Assuming ISO format
                    .orElse("");

            leaderboard.add(new LeaderboardEntry(employeeId, totalScore, latestSubmission));
        }

        // Sort by score descending
        leaderboard.sort((a, b) -> Double.compare(b.getBestScore(), a.getBestScore()));

        return leaderboard.stream().limit(topN).collect(Collectors.toList());
    }
}
