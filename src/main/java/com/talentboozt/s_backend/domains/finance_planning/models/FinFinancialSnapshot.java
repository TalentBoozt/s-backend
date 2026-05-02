package com.talentboozt.s_backend.domains.finance_planning.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.Map;

@Data
@Document(collection = "fin_financial_snapshots")
public class FinFinancialSnapshot {
    @Id
    private String id;
    private String organizationId;
    private String projectId;
    private String scenarioId;
    private String month;
    private Double revenue;
    private Double cost;
    private Double profit;
    private Map<String, Double> breakdown; // storageCost, aiCost, marketingCost
    private Instant computedAt;
}