package com.talentboozt.s_backend.shared.audit.tracking.dto.monitor;

import lombok.Data;
import java.util.List;

@Data
public class FormAnalyticsDTO {
    private String formId;
    private List<FormFieldStats> fieldStats;
    private List<FormAbandonmentStats> abandonmentStats;
    private Long totalSubmissions;
    private Long totalAbandonments;
    private Double completionRate;
}