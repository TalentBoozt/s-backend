package com.talentboozt.s_backend.domains.audit_logs.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Document(collection = "leados_audit_logs")
public class LeadOSAuditLog {
    @Id
    private String id;
    private String workspaceId;
    private String userId;
    
    private String action; // CREATE_CAMPAIGN, EXPORT_LEADS, AI_TEMPLATE_GEN
    private String entityId;
    private String entityType;
    
    private Map<String, Object> details;
    private String ipAddress;
    
    private LocalDateTime timestamp = LocalDateTime.now();
}
