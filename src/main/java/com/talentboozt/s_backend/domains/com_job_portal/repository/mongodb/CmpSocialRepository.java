package com.talentboozt.s_backend.domains.com_job_portal.repository.mongodb;

import com.talentboozt.s_backend.domains.com_job_portal.model.CmpSocialModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CmpSocialRepository extends MongoRepository<CmpSocialModel, String> {
    List<CmpSocialModel> findByCompanyId(String companyId);
    void deleteByCompanyId(String companyId);
}
