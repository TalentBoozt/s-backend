package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.talentboozt.s_backend.domains.edu.model.EAiCredits;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface EAiCreditsRepository extends MongoRepository<EAiCredits, String> {
    Optional<EAiCredits> findByUserId(String userId);
    List<EAiCredits> findByExpiresAtBeforeAndBalanceGreaterThan(Instant expiresAt, Integer balance);
}
