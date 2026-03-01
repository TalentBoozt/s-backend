package com.talentboozt.s_backend.domains.jobs.repository.mongodb;

import com.talentboozt.s_backend.domains.jobs.model.JobPostModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JobPostRepository extends MongoRepository<JobPostModel, String> {
    List<JobPostModel> findByCompanyId(String companyId);

    List<JobPostModel> findByStatus(String status);
}
