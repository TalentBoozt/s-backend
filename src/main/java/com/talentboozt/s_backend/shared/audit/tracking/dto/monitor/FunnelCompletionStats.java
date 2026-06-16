package com.talentboozt.s_backend.shared.audit.tracking.dto.monitor;

import lombok.Data;

@Data
public class FunnelCompletionStats {
    private Long completions;
    private Double avgDuration;
}