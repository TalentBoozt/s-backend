package com.talentboozt.s_backend.shared.audit.tracking.dto.monitor;

import lombok.Data;

@Data
public class ExperimentConversionStats {
    private String variant;
    private Long conversions;
    private Double totalValue;
    private Double avgValue;
}