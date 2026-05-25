package com.talentboozt.s_backend.domains.edu.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * Standardized Organic Search Keyword Metric Entity.
 * Maps search impressions, clicks, CTR, and SERP ranking positions in the MongoDB collection "edu_seo_keyword_metrics".
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_seo_keyword_metrics")
public class ESeoKeywordMetrics {

    @Id
    private String id;

    @Indexed(unique = true)
    private String keyword;

    private Integer clicks;
    private Integer impressions;
    private Double ctr;
    private Double position;
    
    @Builder.Default
    private Date updatedAt = new Date();
}
