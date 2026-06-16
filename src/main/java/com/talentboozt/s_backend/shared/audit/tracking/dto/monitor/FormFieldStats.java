package com.talentboozt.s_backend.shared.audit.tracking.dto.monitor;

import lombok.Data;

@Data
public class FormFieldStats {
    private String fieldName;
    private Long interactions;
    private Double avgTimeSpent;
}