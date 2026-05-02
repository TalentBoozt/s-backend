package com.talentboozt.s_backend.domains.finance_planning.scenario.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "fin_scenario_overrides")
public class ScenarioOverride {
    @Id
    private String id;
    private String scenarioId;
    private String path; // e.g., "sales.pro_users"
    private String month; // optional
    private Object value;
    private OverrideOperation type; // UPDATE, DELETE
    private String versionId;
    private String createdBy;
    private Instant createdAt;

    public enum OverrideOperation {
        UPDATE, DELETE
    }
}
