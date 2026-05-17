package com.talentboozt.s_backend.domains.audit.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "audit_logs")
public class AuditLog {
    @Id
    private String id;
    private String userId;
    private String organizationId;
    private String action; // LOGIN, PERMISSION_CHANGE, STATUS_CHANGE, BILLING_UPDATE, ADMIN_ACTION
    
    private String resourceId;
    private String resourceType; // JOB, APPLICATION, USER, ORGANIZATION, BILLING
    
    private Map<String, Object> oldState;
    private Map<String, Object> newState;
    
    private String ipAddress;
    private String userAgent;
    private Instant timestamp;
    
    private String status; // SUCCESS, FAILURE
    private String errorMessage;
}
