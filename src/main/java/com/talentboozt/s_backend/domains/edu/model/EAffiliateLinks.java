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
@Document(collection = "edu_affiliate_links")
public class EAffiliateLinks {
    @Id
    private String id;
    
    @Indexed
    private String affiliateId;
    
    @Indexed
    private String courseId;
    
    @Indexed(unique = true)
    private String trackingCode;
    
    @Builder.Default
    private Long clicks = 0L;
    
    @CreatedDate
    private Instant createdAt;
}
