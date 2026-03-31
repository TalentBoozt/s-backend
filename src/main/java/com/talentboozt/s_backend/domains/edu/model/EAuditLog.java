package com.talentboozt.s_backend.domains.edu.model;

import java.time.Instant;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_audit_log")
public class EAuditLog {
    @Id
    private String id;
    
    @Indexed
    private String actorId; // UserId of the person performing the action
    
    @Indexed
    private String action; // e.g., UPDATE_COURSE, DELETE_WORKSPACE, APPROVE_PAYOUT
    
    private String targetId; // ID of the entity being modified
    private String targetType; // e.g., COURSE, PAYOUT, WORKSPACE
    
    private String previousState; // JSON blob of the state before
    private String newState; // JSON blob of the state after
    
    private String ipAddress;
    private String userAgent;
    
    @CreatedDate
    @Indexed
    private Instant createdAt;
}
