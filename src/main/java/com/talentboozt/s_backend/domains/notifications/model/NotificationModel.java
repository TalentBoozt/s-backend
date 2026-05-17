package com.talentboozt.s_backend.domains.notifications.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Getter
@Setter
@Document(collection = "notifications")
public class NotificationModel {
    @Id
    private String id;
    private String userId;
    private String title;
    private String message;
    private String type; // JOB_MATCH, APPLICATION_STATUS, SYSTEM
    private boolean read;
    private String actionUrl;
    private Instant createdAt;
}
