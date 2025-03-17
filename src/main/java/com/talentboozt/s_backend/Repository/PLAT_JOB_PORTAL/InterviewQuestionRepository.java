package com.talentboozt.s_backend.Repository.PLAT_JOB_PORTAL;

import com.talentboozt.s_backend.Model.PLAT_JOB_PORTAL.InterviewQuestionModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface InterviewQuestionRepository extends MongoRepository<InterviewQuestionModel, String> {
    Optional<InterviewQuestionModel> findByQuestions_Id(String questionId);
    Optional<InterviewQuestionModel> findByQuestions_Answers_Id(String answerId);
}
