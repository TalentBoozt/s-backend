package com.talentboozt.s_backend.domains._private.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString

@Document(collection = "portal_user_activity")
public class UserActivity {
    @Id
    private String id;
    private String userId;
    private String encryptedIpAddress;
    private LocalDateTime timestamp;
    private String endpointAccessed;
    @Field(targetType = FieldType.DATE_TIME)
    private LocalDateTime lastActive;
    private LocalDateTime sessionStart;
    private LocalDateTime sessionEnd;
    private String country;
    private boolean suspectedVpn;
    private boolean suspectedBot;
    @Indexed(name = "expireAtIndex", expireAfter = "0s")
    private Instant expiresAt;
}
