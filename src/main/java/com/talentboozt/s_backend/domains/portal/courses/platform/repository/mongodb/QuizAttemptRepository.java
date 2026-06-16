package com.talentboozt.s_backend.domains.portal.courses.platform.repository.mongodb;

import com.talentboozt.s_backend.domains.portal.courses.platform.model.QuizAttempt;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface QuizAttemptRepository extends MongoRepository<QuizAttempt, String> {
    List<QuizAttempt> findByEmployeeIdAndQuizId(String employeeId, String quizId);
    List<QuizAttempt> findByQuizIdOrderByScoreDesc(String quizId);
    List<QuizAttempt> findByCourseId(String courseId);
}
