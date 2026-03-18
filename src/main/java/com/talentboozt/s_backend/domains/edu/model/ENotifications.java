package com.talentboozt.s_backend.domains.edu.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.talentboozt.s_backend.domains.edu.enums.ENotificationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_notifications")
@CompoundIndexes({
        @CompoundIndex(name = "user_unread_idx", def = "{'userId': 1, 'isRead': 1}")
})
public class ENotifications {
    @Id
    private String id;

    @Indexed
    private String userId;

    @Indexed
    private String workspaceId;

    @Indexed
    private ENotificationType type;

    private String title;
    private String message;
    private String url;
    private String icon;

    private String actionType; // NAVIGATE, MODAL, NONE
    private String relatedEntityId;
    private String entityType; // COURSE, ENROLLMENT, PAYMENT etc.

    @Indexed
    private Boolean isRead = false;

    @Indexed
    private Boolean isArchived = false;

    private Instant readAt;

    @CreatedDate
    private Instant createdAt;

    @Indexed(expireAfterSeconds = 0)
    private Instant expiresAt;
}
