package com.talentboozt.s_backend.shared.audit.tracking.dto.monitor;

import lombok.Data;
import java.util.List;

@Data
public class FunnelAnalysisDTO {
    private String funnelId;
    private List<FunnelStepStats> steps;
    private FunnelCompletionStats completionStats;
    private Double overallConversionRate;
}