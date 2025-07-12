package com.talentboozt.s_backend.domains.plat_job_portal.repository;

import com.talentboozt.s_backend.domains.plat_job_portal.model.InterviewQuestionModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface InterviewQuestionRepository extends MongoRepository<InterviewQuestionModel, String> {
    Optional<InterviewQuestionModel> findByQuestions_Id(String questionId);
    Optional<InterviewQuestionModel> findByQuestions_Answers_Id(String answerId);
}
