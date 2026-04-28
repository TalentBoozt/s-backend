package com.talentboozt.s_backend.domains.ai_tool.repository.mongodb;

import com.talentboozt.s_backend.domains.ai_tool.model.AIQuota;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AIQuotaRepository extends MongoRepository<AIQuota, String> {
    Optional<AIQuota> findByUserId(String userId);
}
