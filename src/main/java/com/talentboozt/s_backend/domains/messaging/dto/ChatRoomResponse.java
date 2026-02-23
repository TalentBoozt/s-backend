package com.talentboozt.s_backend.domains.messaging.dto;

import com.talentboozt.s_backend.domains.messaging.model.RoomType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ChatRoomResponse {
    private String id;
    private RoomType type;
    private String name;
    private List<ParticipantDTO> participants;
    private String communityId;
    private MessageResponse lastMessage;
    private int unreadCount;
    private LocalDateTime createdAt;

    @com.fasterxml.jackson.annotation.JsonProperty("isPinned")
    private boolean isPinned;
    @com.fasterxml.jackson.annotation.JsonProperty("isArchived")
    private boolean isArchived;
    @com.fasterxml.jackson.annotation.JsonProperty("isFavorite")
    private boolean isFavorite;
}
