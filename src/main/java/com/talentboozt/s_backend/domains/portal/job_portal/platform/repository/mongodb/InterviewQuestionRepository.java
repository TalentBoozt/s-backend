package com.talentboozt.s_backend.domains.portal.job_portal.platform.repository.mongodb;

import com.talentboozt.s_backend.domains.portal.job_portal.platform.model.InterviewQuestionModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface InterviewQuestionRepository extends MongoRepository<InterviewQuestionModel, String> {
    Optional<InterviewQuestionModel> findByQuestions_Id(String questionId);
    Optional<InterviewQuestionModel> findByQuestions_Answers_Id(String answerId);
}
