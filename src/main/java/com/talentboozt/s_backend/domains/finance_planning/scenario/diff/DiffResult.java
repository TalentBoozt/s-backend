package com.talentboozt.s_backend.domains.finance_planning.scenario.diff;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiffResult {
    private String field;
    private String month; // optional
    private Object baseValue;
    private Object scenarioValue;
    private String impact;
    private ImpactType impactType;

    public enum ImpactType {
        POSITIVE, NEGATIVE, NEUTRAL
    }
}
