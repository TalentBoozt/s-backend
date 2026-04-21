package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import com.talentboozt.s_backend.domains.edu.model.EApiKey;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EApiKeyRepository extends MongoRepository<EApiKey, String> {
    Optional<EApiKey> findByApiKey(String apiKey);
    List<EApiKey> findByOwnerId(String ownerId);
}
