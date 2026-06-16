package com.talentboozt.s_backend.shared.audit.tracking.dto.monitor;

import lombok.Data;

@Data
public class ErrorSummaryDTO {
    private String errorMessage;
    private Long count;
    private String lastOccurred;
}