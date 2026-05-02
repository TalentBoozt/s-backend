package com.talentboozt.s_backend.domains.finance_planning.analytics.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "fin_metric_definitions")
public class MetricDefinition {
    @Id
    private String id;
    private String organizationId;
    private String name;
    private String key; // e.g., "profit_margin"
    private String formula; // e.g., "(revenue - cost) / revenue"
    private boolean isCustom;
    private String unit; // percentage, currency, count
}
