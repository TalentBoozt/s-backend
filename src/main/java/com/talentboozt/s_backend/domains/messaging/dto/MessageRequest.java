package com.talentboozt.s_backend.domains.messaging.dto;

import com.talentboozt.s_backend.domains.messaging.model.MessageType;
import lombok.Data;

@Data
public class MessageRequest {
    private String roomId;
    private String content;
    private MessageType messageType;
}
