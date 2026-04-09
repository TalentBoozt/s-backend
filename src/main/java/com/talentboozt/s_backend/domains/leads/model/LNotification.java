package com.talentboozt.s_backend.domains.leads.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "lead_notifications")
public class LNotification {
    @Id
    private String id;
    private String workspaceId;
    private String userId; // Optional: directed to specific user
    private String title;
    private String message;
    private String type; // signal, campaign, system
    private boolean read = false;
    private LocalDateTime createdAt = LocalDateTime.now();
    private String link;
}
