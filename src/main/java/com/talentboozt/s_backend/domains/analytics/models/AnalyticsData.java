package com.talentboozt.s_backend.domains.analytics.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "analytics_collection")
@CompoundIndexes({
    @CompoundIndex(name = "analytics_lookup_idx", def = "{'organizationId': 1, 'projectId': 1, 'scenarioId': 1, 'metric': 1, 'granularity': 1, 'period': 1}")
})
public class AnalyticsData {
    @Id
    private String id;
    private String organizationId;
    private String projectId;
    private String scenarioId;
    private String metric; // revenue, cost, profit, etc.
    private String granularity; // MONTH, QUARTER, YEAR
    private String period; // 2026-01, 2026-Q1, 2026
    private Double value;
    private Map<String, Object> metadata;
    private Instant computedAt;
}
