package com.talentboozt.s_backend.domains.finance_planning.models;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data
@Builder
@Document(collection = "fin_audit_logs")
public class FinAuditLog {
    @Id
    private String id;
    private String organizationId;
    private String projectId;
    private String userId;
    private String action; // CREATE, UPDATE, DELETE
    private String entityType; // ASSUMPTION, SALES, PRICING, BUDGET
    private String entityId;
    private Object previousValue;
    private Object newValue;
    private Instant timestamp;
    private String ipAddress;
}
