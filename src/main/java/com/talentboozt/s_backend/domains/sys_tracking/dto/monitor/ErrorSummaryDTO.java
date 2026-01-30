package com.talentboozt.s_backend.domains.sys_tracking.dto.monitor;

import lombok.Data;

@Data
public class ErrorSummaryDTO {
    private String errorMessage;
    private Long count;
    private String lastOccurred;
}