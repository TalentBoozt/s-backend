package com.talentboozt.s_backend.Repository.PLAT_JOB_PORTAL;

import com.talentboozt.s_backend.Model.PLAT_JOB_PORTAL.JobApplyModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface JobApplyRepository extends MongoRepository<JobApplyModel, String> {
    Optional<List<JobApplyModel>> findAllByCompanyId(String companyId);

    Optional<List<JobApplyModel>> findAllByJobId(String jobId);

    Optional<JobApplyModel> findByJobId(String id);
}
