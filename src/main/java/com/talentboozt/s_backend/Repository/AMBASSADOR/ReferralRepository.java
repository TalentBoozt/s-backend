package com.talentboozt.s_backend.Repository.AMBASSADOR;

import com.talentboozt.s_backend.Model.AMBASSADOR.ReferralModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReferralRepository extends MongoRepository<ReferralModel, String> {
    List<ReferralModel> findAllByReferralCode(String referralCode);

    List<ReferralModel> findAllByAmbassadorId(String ambassadorId);

    int countByAmbassadorId(String id);
}
