package com.talentboozt.s_backend.domains.community.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "community_notifications")
public class Notification {
    @Id
    private String id;
    private String recipientId;
    private String senderId;
    private NotificationType type;
    private String targetId; // ID of post or comment
    private boolean isRead;
    private LocalDateTime timestamp;

    public enum NotificationType {
        LIKE,
        COMMENT,
        MENTION,
        FOLLOW,
        QUOTE,
        BAN,
        UNBAN,
        POST_REACTION,
        COMMENT_REACTION,
        POST_SHARE,
        COMMUNITY_POST,
        COMMUNITY_MEMBER_JOIN,
        COMMUNITY_ROLE_CHANGE
    }
}
