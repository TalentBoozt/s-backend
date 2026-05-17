package com.talentboozt.s_backend.domains.communication.model;

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
@Document(collection = "job_portal_messages")
public class CommunicationMessageModel {
    @Id
    private String id;
    private String threadId;
    private String senderId;
    private String receiverId;
    
    private String content;
    private List<String> attachmentUrls;
    
    private boolean read;
    private Instant readAt;
    
    private Instant timestamp;
}
