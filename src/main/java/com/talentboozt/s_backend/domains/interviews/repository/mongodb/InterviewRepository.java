package com.talentboozt.s_backend.domains.interviews.repository.mongodb;

import com.talentboozt.s_backend.domains.interviews.model.InterviewModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface InterviewRepository extends MongoRepository<InterviewModel, String> {
    List<InterviewModel> findByJobId(String jobId);
    List<InterviewModel> findByCandidateId(String candidateId);
    List<InterviewModel> findByInterviewerIdsContaining(String interviewerId);
}
