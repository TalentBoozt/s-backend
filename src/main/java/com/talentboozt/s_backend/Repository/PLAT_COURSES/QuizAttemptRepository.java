package com.talentboozt.s_backend.Repository.PLAT_COURSES;

import com.talentboozt.s_backend.Model.PLAT_COURSES.QuizAttempt;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface QuizAttemptRepository extends MongoRepository<QuizAttempt, String> {
    List<QuizAttempt> findByEmployeeIdAndQuizId(String employeeId, String quizId);
    List<QuizAttempt> findByQuizIdOrderByScoreDesc(String quizId);
}
