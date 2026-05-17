package com.talentboozt.s_backend.domains.insights.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "insights")
public class InsightModel {
    @Id
    private String id;
    private String organizationId;
    private String userId;
    
    private String type; // PERFORMANCE, TREND, PREDICTION, SUGGESTION
    private String title;
    private String content;
    private String impact; // HIGH, MEDIUM, LOW
    
    private Map<String, Object> dataPoints;
    
    private Instant createdAt;
}
