package com.talentboozt.s_backend.domains.ambassador.repository.mongodb;

import com.talentboozt.s_backend.domains.ambassador.model.AmbReferralModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AmbReferralRepository extends MongoRepository<AmbReferralModel, String> {
    List<AmbReferralModel> findAllByReferralCode(String referralCode);

    List<AmbReferralModel> findAllByAmbassadorId(String ambassadorId);

    int countByAmbassadorId(String id);
}
