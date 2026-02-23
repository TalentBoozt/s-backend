package com.talentboozt.s_backend.domains.messaging.dto;

import com.talentboozt.s_backend.domains.messaging.model.MessageType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
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

    @com.fasterxml.jackson.annotation.JsonProperty("isEdited")
    private boolean isEdited;
    private LocalDateTime updatedAt;

    @com.fasterxml.jackson.annotation.JsonProperty("isDeleted")
    private boolean isDeleted;
    private List<String> deletedForUsers;
    private Map<String, List<String>> reactions;
    private Map<String, Object> metadata;

    @com.fasterxml.jackson.annotation.JsonProperty("isForwarded")
    private boolean isForwarded;
    private String forwardedFromId;

    @com.fasterxml.jackson.annotation.JsonProperty("isPinned")
    private boolean isPinned;
    private Instant expiresAt;

    @com.fasterxml.jackson.annotation.JsonProperty("isEncrypted")
    private boolean isEncrypted;
    private String replyToId;
    private MessageResponse replyToMessage;
}
