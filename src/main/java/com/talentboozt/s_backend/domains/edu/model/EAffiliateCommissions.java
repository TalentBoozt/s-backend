package com.talentboozt.s_backend.domains.edu.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_affiliate_commissions")
public class EAffiliateCommissions {
    @Id
    private String id;
    
    @Indexed
    private String affiliateId;
    
    @Indexed
    private String transactionId;
    
    private String courseId;
    private Double amount;
    private String currency;
    
    @CreatedDate
    private Instant createdAt;
}
