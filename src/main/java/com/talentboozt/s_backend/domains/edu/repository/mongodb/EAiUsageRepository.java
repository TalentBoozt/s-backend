package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.talentboozt.s_backend.domains.edu.model.EAiUsage;
import java.util.List;

@Repository
public interface EAiUsageRepository extends MongoRepository<EAiUsage, String> {
    List<EAiUsage> findByUserId(String userId);
}
