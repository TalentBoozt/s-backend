package com.talentboozt.s_backend.domains.jobs.repository.mongodb;

import com.talentboozt.s_backend.domains.jobs.model.ApplicantModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ApplicantRepository extends MongoRepository<ApplicantModel, String> {
    List<ApplicantModel> findByJobId(String jobId);
    List<ApplicantModel> findByCompanyId(String companyId);
    List<ApplicantModel> findByCandidateId(String candidateId);
}
