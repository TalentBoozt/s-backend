package com.talentboozt.s_backend.domains.portal.job_portal.company.repository.mongodb;

import com.talentboozt.s_backend.domains.portal.job_portal.company.model.CmpPostedJobsModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CmpPostedJobsRepository extends MongoRepository<CmpPostedJobsModel, String> {

    List<CmpPostedJobsModel> findByCompanyId(String companyId);
}
