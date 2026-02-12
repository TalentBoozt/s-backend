package com.talentboozt.s_backend.domains.messaging.dto;

import com.talentboozt.s_backend.domains.messaging.model.MessageType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class MessageResponse {
    private String id;
    private String roomId;
    private String senderId;
    private String content;
    private MessageType messageType;
    private LocalDateTime createdAt;
    private Map<String, LocalDateTime> readByUsers;
}
