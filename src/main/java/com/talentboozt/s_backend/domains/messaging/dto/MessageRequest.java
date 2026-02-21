package com.talentboozt.s_backend.domains.messaging.dto;

import java.time.Instant;
import java.util.Map;

import com.talentboozt.s_backend.domains.messaging.model.MessageType;
import lombok.Data;

@Data
public class MessageRequest {
    private String roomId;
    private String content;
    private MessageType messageType;
    private Map<String, Object> metadata;
    private boolean isEncrypted;
    private Instant expiresAt;
}
