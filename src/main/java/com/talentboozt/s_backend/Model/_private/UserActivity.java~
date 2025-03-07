package com.hris.HRIS_job_portal.Model._private;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
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
}
