package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.dto.evaluation.EQuestionDTO;
import com.talentboozt.s_backend.domains.edu.dto.evaluation.QuizAttemptRequest;
import com.talentboozt.s_backend.domains.edu.dto.evaluation.QuizRequest;
import com.talentboozt.s_backend.domains.edu.enums.EGradingStatus;
import com.talentboozt.s_backend.domains.edu.model.EQuizAttempts;
import com.talentboozt.s_backend.domains.edu.model.EQuizzes;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EQuizAttemptsRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EQuizzesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

@Service
public class EduQuizService {

    private final EQuizzesRepository quizzesRepository;
    private final EQuizAttemptsRepository attemptsRepository;

    public EduQuizService(EQuizzesRepository quizzesRepository, EQuizAttemptsRepository attemptsRepository) {
        this.quizzesRepository = quizzesRepository;
        this.attemptsRepository = attemptsRepository;
    }

    public EQuizzes createQuiz(String courseId, String sectionId, String creatorId, QuizRequest request) {
        if (request.getQuestions() != null) {
            request.getQuestions().forEach(q -> {
                if (q.getId() == null)
                    q.setId(UUID.randomUUID().toString());
            });
        }

        EQuizzes quiz = EQuizzes.builder()
                .courseId(courseId)
                .sectionId(sectionId)
                .title(request.getTitle())
                .description(request.getDescription())
                .type(request.getType())
                .durationLimit(request.getDurationLimit())
                .passingScore(request.getPassingScore())
                .questions(request.getQuestions())
                .isPublished(request.getIsPublished() != null ? request.getIsPublished() : false)
                .allowRetakes(request.getAllowRetakes() != null ? request.getAllowRetakes() : 0)
                .createdBy(creatorId)
                .createdAt(Instant.now())
                .build();
        return quizzesRepository.save(quiz);
    }

    public EQuizzes getQuiz(String id) {
        return quizzesRepository.findById(id).orElseThrow(() -> new RuntimeException("Quiz not found"));
    }

    public EQuizzes updateQuiz(String id, QuizRequest request) {
        EQuizzes quiz = getQuiz(id);
        if (request.getTitle() != null)
            quiz.setTitle(request.getTitle());
        if (request.getDescription() != null)
            quiz.setDescription(request.getDescription());
        if (request.getType() != null)
            quiz.setType(request.getType());
        if (request.getDurationLimit() != null)
            quiz.setDurationLimit(request.getDurationLimit());
        if (request.getPassingScore() != null)
            quiz.setPassingScore(request.getPassingScore());
        if (request.getQuestions() != null) {
            request.getQuestions().forEach(q -> {
                if (q.getId() == null)
                    q.setId(UUID.randomUUID().toString());
            });
            quiz.setQuestions(request.getQuestions());
        }
        if (request.getIsPublished() != null)
            quiz.setIsPublished(request.getIsPublished());
        if (request.getAllowRetakes() != null)
            quiz.setAllowRetakes(request.getAllowRetakes());

        quiz.setUpdatedAt(Instant.now());
        return quizzesRepository.save(quiz);
    }

    public void deleteQuiz(String id) {
        quizzesRepository.deleteById(id);
    }

    @Transactional
    public EQuizAttempts submitQuizAttempt(String quizId, String userId, QuizAttemptRequest request) {
        EQuizzes quiz = getQuiz(quizId);

        // Very basic auto-grading for multiple choice
        double earnedPoints = 0.0;
        double totalPoints = 0.0;

        if (quiz.getQuestions() != null) {
            for (EQuestionDTO question : quiz.getQuestions()) {
                double qPoints = question.getPoints() != null ? question.getPoints() : 1.0;
                totalPoints += qPoints;

                String[] correctAnswers = question.getCorrectAnswers();
                String[] userAns = request.getUserAnswers() != null ? request.getUserAnswers().get(question.getId())
                        : null;

                if (correctAnswers != null && userAns != null && Arrays.equals(correctAnswers, userAns)) {
                    earnedPoints += qPoints;
                }
            }
        }

        double percentage = totalPoints > 0 ? (earnedPoints / totalPoints) * 100.0 : 0.0;

        EGradingStatus status;
        if (quiz.getPassingScore() != null) {
            status = percentage >= quiz.getPassingScore() ? EGradingStatus.PASSED : EGradingStatus.FAILED;
        } else {
            status = EGradingStatus.GRADED;
        }

        EQuizAttempts attempt = EQuizAttempts.builder()
                .userId(userId)
                .quizId(quizId)
                .userAnswers(request.getUserAnswers())
                .score(earnedPoints)
                .percentage(percentage)
                .status(status)
                .isLatest(true)
                .completedAt(Instant.now())
                .createdAt(Instant.now())
                .build();

        // In reality, we should mark previous attempts as isLatest=false
        return attemptsRepository.save(attempt);
    }
}
