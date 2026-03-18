package com.talentboozt.s_backend.domains.edu.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.talentboozt.s_backend.domains.edu.enums.EPayoutMethod;
import com.talentboozt.s_backend.domains.edu.enums.EPayoutStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_payouts")
public class EPayouts {
    @Id
    private String id;
    
    @Indexed
    private String creatorId;
    
    private Double amount;
    private String currency = "USD";
    
    @Indexed
    private EPayoutMethod method;
    
    @Indexed
    private EPayoutStatus status;
    
    private String transactionReference;
    private String bankDetails; // Should be encrypted
    private String paypalEmail;
    private Double platformFee;
    
    @CreatedDate
    private Instant createdAt;
    
    private Instant paidAt;
    private Instant requestedAt;
}
