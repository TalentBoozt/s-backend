package com.talentboozt.s_backend.domains.messaging.model;

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
@Document(collection = "user_presence")
public class UserPresence {
    @Id
    private String userId;
    private PresenceStatus status;
    private LocalDateTime lastSeen;
}
