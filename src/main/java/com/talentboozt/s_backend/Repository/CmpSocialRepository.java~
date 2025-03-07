package com.hris.HRIS_job_portal.Repository;

import com.hris.HRIS_job_portal.Model.CmpSocialModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CmpSocialRepository extends MongoRepository<CmpSocialModel, String> {
    List<CmpSocialModel> findByCompanyId(String companyId);
    void deleteByCompanyId(String companyId);
}
