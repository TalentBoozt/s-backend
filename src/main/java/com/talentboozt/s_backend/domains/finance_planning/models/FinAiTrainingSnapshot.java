package com.talentboozt.s_backend.domains.finance_planning.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.List;

@Data
@Document(collection = "fin_ai_training_snapshots")
public class FinAiTrainingSnapshot {
    @Id
    private String id;
    private String organizationId;
    private String projectId;
    private String scenarioId;
    private String userId;
    private String inputSnapshot; // JSON of assumptions, sales, pricing, budget
    private String outputSnapshot; // JSON of revenue, cost, profit
    private List<String> changedFields;
    private List<String> tags;
    private Instant createdAt;
}