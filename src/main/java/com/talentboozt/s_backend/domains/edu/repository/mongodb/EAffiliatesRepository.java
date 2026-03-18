package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.talentboozt.s_backend.domains.edu.model.EAffiliates;
import java.util.Optional;

@Repository
public interface EAffiliatesRepository extends MongoRepository<EAffiliates, String> {
    Optional<EAffiliates> findByReferralCode(String referralCode);
    Optional<EAffiliates> findByUserId(String userId);
}
