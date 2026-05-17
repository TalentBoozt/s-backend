package com.talentboozt.s_backend.domains.ai_orchestration.repository.mongodb;

import com.talentboozt.s_backend.domains.ai_orchestration.model.AiTask;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AiTaskRepository extends MongoRepository<AiTask, String> {
}
