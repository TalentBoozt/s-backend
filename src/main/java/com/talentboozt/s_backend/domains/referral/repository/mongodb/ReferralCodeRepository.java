package com.talentboozt.s_backend.domains.referral.repository.mongodb;

import com.talentboozt.s_backend.domains.referral.model.ReferralCode;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReferralCodeRepository extends MongoRepository<ReferralCode, String> {
    Optional<ReferralCode> findByCode(String code);
    Optional<ReferralCode> findByUserId(String userId);
}
