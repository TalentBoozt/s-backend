package com.talentboozt.s_backend.shared.ai.orchestration.repository.mongodb;

import com.talentboozt.s_backend.shared.ai.orchestration.model.AiTask;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AiTaskRepository extends MongoRepository<AiTask, String> {
}
