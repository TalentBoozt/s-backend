package com.talentboozt.s_backend.shared.audit.tracking.dto.monitor;

import lombok.Data;

@Data
public class ScreenResolutionCount {
    private String resolution;
    private Long count;
}