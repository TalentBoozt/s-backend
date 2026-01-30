package com.talentboozt.s_backend.domains.sys_tracking.dto.monitor;

import lombok.Data;

@Data
public class FormAbandonmentStats {
    private String fieldName;
    private Long abandonments;
}