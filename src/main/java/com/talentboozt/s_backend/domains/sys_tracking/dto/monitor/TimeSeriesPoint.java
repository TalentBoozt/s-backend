package com.talentboozt.s_backend.domains.sys_tracking.dto.monitor;

import lombok.Data;

@Data
public class TimeSeriesPoint {
    private String timestamp;
    private Long count;
}