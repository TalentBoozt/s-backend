package com.talentboozt.s_backend.domains.portal.job_portal.platform.repository.mongodb;

import com.talentboozt.s_backend.domains.portal.job_portal.platform.model.JobApplyModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface JobApplyRepository extends MongoRepository<JobApplyModel, String> {
    Optional<List<JobApplyModel>> findAllByCompanyId(String companyId);

    Optional<List<JobApplyModel>> findAllByJobId(String jobId);

    Optional<JobApplyModel> findByJobId(String id);

    @org.springframework.data.mongodb.repository.Query("{ 'applicants.employeeId': ?0 }")
    List<JobApplyModel> findByEmployeeIdInApplicants(String employeeId);
}
