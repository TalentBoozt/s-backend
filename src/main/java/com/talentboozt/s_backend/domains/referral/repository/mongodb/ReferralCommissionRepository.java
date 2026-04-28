package com.talentboozt.s_backend.domains.referral.repository.mongodb;

import com.talentboozt.s_backend.domains.referral.model.ReferralCommission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReferralCommissionRepository extends MongoRepository<ReferralCommission, String> {
    Optional<ReferralCommission> findByReferredCreatorId(String referredCreatorId);
}
