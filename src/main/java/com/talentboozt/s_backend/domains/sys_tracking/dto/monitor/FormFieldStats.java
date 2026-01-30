package com.talentboozt.s_backend.domains.sys_tracking.dto.monitor;

import lombok.Data;

@Data
public class FormFieldStats {
    private String fieldName;
    private Long interactions;
    private Double avgTimeSpent;
}