package com.talentboozt.s_backend.shared.ai.assistant.repository.mongodb;

import com.talentboozt.s_backend.shared.ai.assistant.model.AiAssistantSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface AiAssistantSessionRepository extends MongoRepository<AiAssistantSession, String> {
    Optional<AiAssistantSession> findByUserIdAndRole(String userId, String role);
}
