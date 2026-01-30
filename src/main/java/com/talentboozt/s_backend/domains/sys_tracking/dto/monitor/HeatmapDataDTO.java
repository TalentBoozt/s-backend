package com.talentboozt.s_backend.domains.sys_tracking.dto.monitor;

import lombok.Data;
import java.util.List;

@Data
public class HeatmapDataDTO {
    private String url;
    private List<HeatmapPoint> clicks;
    private List<HeatmapPoint> movements;
}