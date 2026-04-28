package com.talentboozt.s_backend.domains.ai_tool.repository.mongodb;

import com.talentboozt.s_backend.domains.ai_tool.model.AIUsage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AIUsageRepository extends MongoRepository<AIUsage, String> {
    List<AIUsage> findByUserId(String userId);
    Page<AIUsage> findByUserId(String userId, Pageable pageable);
}
