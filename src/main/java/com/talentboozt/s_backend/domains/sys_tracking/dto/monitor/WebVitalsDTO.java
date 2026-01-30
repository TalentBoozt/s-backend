package com.talentboozt.s_backend.domains.sys_tracking.dto.monitor;

import lombok.Data;

@Data
public class WebVitalsDTO {
    private String metric; // 'lcp' or 'fid'
    private Double avgValue;
    private Double p75;
}