package com.talentboozt.s_backend.domains.sys_tracking.dto.monitor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceMetricsDTO {
    private double avgDomLoadTime;
    private double avgFullLoadTime;
    private double avgTtfb;
}
