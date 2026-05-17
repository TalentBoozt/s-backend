package com.talentboozt.s_backend.domains.ai_assistant.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ai_assistant_sessions")
public class AiAssistantSession {
    @Id
    private String id;
    private String userId;
    private String role; // APPLICANT, RECRUITER
    
    private List<ChatMessage> history;
    private String context; // E.g., "Reviewing candidate for Senior Engineer"
    
    private Instant createdAt;
    private Instant updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessage {
        private String role; // USER, ASSISTANT, SYSTEM
        private String content;
        private List<String> suggestions;
        private Instant timestamp;
    }
}
