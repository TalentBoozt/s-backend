package com.talentboozt.s_backend.Repository.COM_JOB_PORTAL;

import com.talentboozt.s_backend.Model.COM_JOB_PORTAL.CmpSocialModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CmpSocialRepository extends MongoRepository<CmpSocialModel, String> {
    List<CmpSocialModel> findByCompanyId(String companyId);
    void deleteByCompanyId(String companyId);
}
