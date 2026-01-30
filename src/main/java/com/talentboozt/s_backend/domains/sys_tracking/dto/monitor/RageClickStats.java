package com.talentboozt.s_backend.domains.sys_tracking.dto.monitor;

import lombok.Data;

@Data
public class RageClickStats {
    private String elementType;
    private String elementText;
    private Long count;
    private Double avgClicks;
}