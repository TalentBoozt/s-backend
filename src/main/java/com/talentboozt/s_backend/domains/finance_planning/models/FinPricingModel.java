package com.talentboozt.s_backend.domains.finance_planning.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data
@Document(collection = "pricing_model_collection")
public class FinPricingModel {
    @Id
    private String id;
    private String organizationId;
    private String projectId;
    private String tier; // free/pro/premium
    private Double price;
    private Double costPerUser;
    private Double margin;
    private Instant effectiveDate;
}