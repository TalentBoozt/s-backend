package com.talentboozt.s_backend.domains.sys_tracking.dto.monitor;

import lombok.Data;

@Data
public class FunnelStepStats {
    private Integer stepIndex;
    private String stepName;
    private Long count;
    private Double dropoffRate; // Calculated
}