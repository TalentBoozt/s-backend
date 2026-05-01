package com.talentboozt.s_backend.domains.finance_planning.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.List;

@Data
@Document(collection = "scenario_collection")
public class Scenario {
    @Id
    private String id;
    private String organizationId;
    private String projectId;
    private String name;
    private Integer baseVersion;
    private List<AssumptionOverride> overrides;
    private Instant createdAt;

    @Data
    public static class AssumptionOverride {
        private String key;
        private String value;
    }
}