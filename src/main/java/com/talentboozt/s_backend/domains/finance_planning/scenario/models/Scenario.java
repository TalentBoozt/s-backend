package com.talentboozt.s_backend.domains.finance_planning.scenario.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "scenarios")
public class Scenario {
    @Id
    private String id;
    private String organizationId;
    private String projectId;
    private String name;
    private String parentScenarioId; // null for root
    private String baseVersionId;
    private String createdBy;
    private Instant createdAt;
    private List<String> tags;
    private String notes;
}
