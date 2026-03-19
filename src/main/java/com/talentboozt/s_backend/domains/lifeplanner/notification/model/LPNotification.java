package com.talentboozt.s_backend.domains.lifeplanner.notification.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data
@Document(collection = "lp_notifications")
public class LPNotification {
    @Id
    private String id;
    @Indexed
    private String userId;
    private String title;
    private String message;
    private String type; // REMINDER, ALERT, NUDGE
    private boolean isRead;
    private Instant createdAt;
}
