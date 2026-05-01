package com.talentboozt.s_backend.domains.finance_planning.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.Map;

@Data
@Document(collection = "budget_collection")
public class FinBudget {
    @Id
    private String id;
    private String organizationId;
    private String projectId;
    private String category;
    private String type; // fixed/variable
    private Map<String, Double> monthlyAllocations; // month -> amount
    private String formula; // Dynamic calculation formula
    private Instant createdAt;
}