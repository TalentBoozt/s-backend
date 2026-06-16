package com.talentboozt.s_backend.shared.audit.tracking.dto.monitor;

import lombok.Data;

@Data
public class HeatmapPoint {
    private Integer x;
    private Integer y;
    private Integer intensity; // Number of clicks/moves at this point
}