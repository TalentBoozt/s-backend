package com.talentboozt.s_backend.domains.lifeplanner.ai.cache;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AICacheEntryRepository extends MongoRepository<AICacheEntry, String> {
    Optional<AICacheEntry> findByCacheKey(String cacheKey);
}
