package com.talentboozt.s_backend.domains.audit_logs.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Document(collection = "client_act_audit_log")
public class ClientActAuditLog {
    @Id
    private String userId;
    private String ipAddress;
    private String sessionId;
    private String action;
    private String source; // e.g., "IpCaptureFilter"
    private Map<String, Object> details;
    private LocalDateTime timestamp;
    @Indexed(name = "expireAtIndex", expireAfter = "0s")
    private Instant expiresAt;

    public ClientActAuditLog() {}

    public ClientActAuditLog(String userId, String ipAddress, String sessionId, String action, String source, Map<String, Object> details, Instant expiresAt) {
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.sessionId = sessionId;
        this.action = action;
        this.source = source;
        this.details = details;
        this.timestamp = LocalDateTime.now();
        this.expiresAt = expiresAt;
    }
}
