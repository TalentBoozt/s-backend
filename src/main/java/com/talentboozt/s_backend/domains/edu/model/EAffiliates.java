package com.talentboozt.s_backend.domains.edu.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.talentboozt.s_backend.domains.edu.enums.EAffiliateStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_affiliates")
public class EAffiliates {
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String userId;
    
    @Indexed(unique = true)
    private String referralCode;
    
    private Double commissionRate; // Example: 0.10 for 10%
    private Double totalEarnings;
    
    @Indexed
    private EAffiliateStatus status;
    
    @CreatedDate
    private Instant createdAt;
    
    @LastModifiedDate
    private Instant updatedAt;
}
