package com.talentboozt.s_backend.shared.audit.tracking.dto.monitor;

import lombok.Data;

@Data
public class ExperimentVariantCount {
    private String variant;
    private Long count;
}