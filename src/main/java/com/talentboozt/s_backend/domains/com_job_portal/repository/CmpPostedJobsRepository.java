package com.talentboozt.s_backend.domains.com_job_portal.repository;

import com.talentboozt.s_backend.domains.com_job_portal.model.CmpPostedJobsModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CmpPostedJobsRepository extends MongoRepository<CmpPostedJobsModel, String> {

    List<CmpPostedJobsModel> findByCompanyId(String companyId);
}
