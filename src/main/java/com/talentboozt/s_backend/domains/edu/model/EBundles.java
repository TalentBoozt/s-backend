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
@Document(collection = "edu_bundles")
public class EBundles {
    @Id
    private String id;
    
    @Indexed
    private String creatorId;
    
    private String name;
    
    private String[] courseIds;
    
    private Double bundlePrice;
    private Double originalTotalPrice;
    private Double savingsPercent;
    
    @Builder.Default
    private String status = "ACTIVE"; // ACTIVE, DRAFT, ARCHIVED
    
    @Builder.Default
    private Integer totalSales = 0;
    
    @CreatedDate
    private Instant createdAt;
}
