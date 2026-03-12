package com.talentboozt.s_backend.domains.lifeplanner.credits.repository.mongodb;

import com.talentboozt.s_backend.domains.lifeplanner.credits.model.UserCredits;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCreditsRepository extends MongoRepository<UserCredits, String> {
    Optional<UserCredits> findByUserId(String userId);
    long countByTier(com.talentboozt.s_backend.domains.lifeplanner.credits.model.SubscriptionTier tier);
}
