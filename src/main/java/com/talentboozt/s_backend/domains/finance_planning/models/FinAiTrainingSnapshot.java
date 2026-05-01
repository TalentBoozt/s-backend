package com.talentboozt.s_backend.domains.finance_planning.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.List;

@Data
@Document(collection = "ai_training_collection")
public class FinAiTrainingSnapshot {
    @Id
    private String id;
    private String organizationId;
    private String projectId;
    private String inputSnapshot; // JSON
    private String outputSnapshot; // JSON
    private List<String> tags; // "high_growth", "loss"
    private Instant createdAt;
}