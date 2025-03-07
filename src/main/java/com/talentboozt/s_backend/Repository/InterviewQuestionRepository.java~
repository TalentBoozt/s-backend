package com.hris.HRIS_job_portal.Repository;

import com.hris.HRIS_job_portal.Model.InterviewQuestionModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface InterviewQuestionRepository extends MongoRepository<InterviewQuestionModel, String> {
    Optional<InterviewQuestionModel> findByQuestions_Id(String questionId);
    Optional<InterviewQuestionModel> findByQuestions_Answers_Id(String answerId);
}
