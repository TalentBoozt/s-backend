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
@Document(collection = "edu_coupons")
public class ECoupons {
    @Id
    private String id;
    
    @Indexed
    private String creatorId;
    
    @Indexed(unique = true)
    private String code;
    
    private String discountType; // PERCENTAGE or FLAT
    private Double discountValue;
    
    private Integer maxRedemptions;
    @Builder.Default
    private Integer currentRedemptions = 0;
    
    private String[] applicableCourseIds;
    
    @Builder.Default
    private Boolean isActive = true;
    
    private Instant expiresAt;
    
    @CreatedDate
    private Instant createdAt;
}
