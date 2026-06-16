package com.talentboozt.s_backend.shared.audit.tracking.dto.monitor;

import lombok.Data;

@Data
public class TimeSeriesPoint {
    private String timestamp;
    private Long count;
}