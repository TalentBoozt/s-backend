package com.talentboozt.s_backend.domains.sys_tracking.dto.monitor;

import lombok.Data;

@Data
public class FunnelCompletionStats {
    private Long completions;
    private Double avgDuration;
}