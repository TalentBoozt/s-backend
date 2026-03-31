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
@Document(collection = "edu_fraud_flags")
public class EFraudFlag {
    @Id
    private String id;
    
    @Indexed
    private String targetUserId; // The user being flagged
    
    private String flagType; // e.g., SELF_BUY, COUPON_ABUSE, ANOMALY_DETECTION
    private String severity; // LOW, MEDIUM, HIGH, CRITICAL
    
    private String evidenceBlob; // JSON blob containing evidence (e.g., transaction IDs)
    
    @Indexed
    private String status; // PENDING_REVIEW, RESOLVED_NO_FRAUD, BANNED_USER
    
    private String reviewerId; // Admin ID
    private String resolutionNotes;
    
    @CreatedDate
    @Indexed
    private Instant createdAt;
    
    private Instant resolvedAt;
}
