package com.talentboozt.s_backend.Repository;

import com.talentboozt.s_backend.Model.CmpSocialModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CmpSocialRepository extends MongoRepository<CmpSocialModel, String> {
    List<CmpSocialModel> findByCompanyId(String companyId);
    void deleteByCompanyId(String companyId);
}
