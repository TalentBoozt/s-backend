package com.talentboozt.s_backend.DTO.SYS_TRACKING.monitor;

public record SessionError(
        String errorMessage,
        String errorSource,
        Integer errorLine,
        Integer errorColumn,
        String rejectionReason
) {}
