package com.talentboozt.s_backend.domains.referral.repository.mongodb;

import com.talentboozt.s_backend.domains.referral.model.Referral;
import com.talentboozt.s_backend.domains.referral.enums.ReferralStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReferralRepository extends MongoRepository<Referral, String> {
    Optional<Referral> findByReferredUserId(String referredUserId);
    List<Referral> findAllByReferrerId(String referrerId);
    boolean existsByReferrerIdAndReferredUserId(String referrerId, String referredUserId);
}
