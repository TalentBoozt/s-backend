package com.talentboozt.s_backend.domains.ambassador.repository;

import com.talentboozt.s_backend.domains.ambassador.model.ReferralModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReferralRepository extends MongoRepository<ReferralModel, String> {
    List<ReferralModel> findAllByReferralCode(String referralCode);

    List<ReferralModel> findAllByAmbassadorId(String ambassadorId);

    int countByAmbassadorId(String id);
}
