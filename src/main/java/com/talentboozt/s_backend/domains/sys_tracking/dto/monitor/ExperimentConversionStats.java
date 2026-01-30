package com.talentboozt.s_backend.domains.sys_tracking.dto.monitor;

import lombok.Data;

@Data
public class ExperimentConversionStats {
    private String variant;
    private Long conversions;
    private Double totalValue;
    private Double avgValue;
}