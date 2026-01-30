package com.talentboozt.s_backend.domains.sys_tracking.dto.monitor;

import lombok.Data;

@Data
public class PerformanceMetricsDTO {
    private Double avgDomLoadTime;
    private Double avgFullLoadTime;
    private Double avgTtfb;
    private Double avgDnsTime;
    private Double avgTcpTime;
    private Double avgDownloadTime;
}