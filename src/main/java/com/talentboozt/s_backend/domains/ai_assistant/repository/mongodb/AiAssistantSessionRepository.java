package com.talentboozt.s_backend.domains.ai_assistant.repository.mongodb;

import com.talentboozt.s_backend.domains.ai_assistant.model.AiAssistantSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface AiAssistantSessionRepository extends MongoRepository<AiAssistantSession, String> {
    Optional<AiAssistantSession> findByUserIdAndRole(String userId, String role);
}
