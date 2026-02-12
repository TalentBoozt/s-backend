package com.talentboozt.s_backend.domains.messaging.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "messages")
public class Message {
    @Id
    private String id;

    @Indexed
    private String roomId;

    private String senderId;
    private String content;
    private MessageType messageType;
    private LocalDateTime createdAt;

    // userId -> readAt
    private Map<String, LocalDateTime> readByUsers;
}
