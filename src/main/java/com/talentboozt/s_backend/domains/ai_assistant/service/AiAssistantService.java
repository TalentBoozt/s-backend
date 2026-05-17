package com.talentboozt.s_backend.domains.ai_assistant.service;

import com.talentboozt.s_backend.domains.ai_assistant.model.AiAssistantSession;
import com.talentboozt.s_backend.domains.ai_assistant.repository.mongodb.AiAssistantSessionRepository;
import com.talentboozt.s_backend.domains.ai_orchestration.service.AiOrchestratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiAssistantService {
    private final AiAssistantSessionRepository repository;
    private final AiOrchestratorService aiOrchestrator;

    public AiAssistantSession getOrCreateSession(String userId, String role) {
        return repository.findByUserIdAndRole(userId, role)
                .orElseGet(() -> repository.save(AiAssistantSession.builder()
                        .userId(userId)
                        .role(role)
                        .history(new ArrayList<>())
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build()));
    }

    public AiAssistantSession processMessage(String sessionId, String content) {
        AiAssistantSession session = repository.findById(sessionId).orElseThrow();
        
        // 1. Add user message
        session.getHistory().add(AiAssistantSession.ChatMessage.builder()
                .role("USER")
                .content(content)
                .timestamp(Instant.now())
                .build());

        // 2. Mock AI response (In production, this calls AiOrchestrator)
        String aiResponse = "I can help you with that. Based on the current candidate pipeline, I recommend moving Sarah to the technical stage.";
        List<String> suggestions = List.of("Move Sarah to Technical", "Send rejection to Michael", "Schedule interview");

        session.getHistory().add(AiAssistantSession.ChatMessage.builder()
                .role("ASSISTANT")
                .content(aiResponse)
                .suggestions(suggestions)
                .timestamp(Instant.now())
                .build());

        session.setUpdatedAt(Instant.now());
        return repository.save(session);
    }
}
