package com.talentboozt.s_backend.domains.sys_tracking.dto.monitor;

public record SessionError(
        String errorMessage,
        String errorSource,
        Integer errorLine,
        Integer errorColumn,
        String rejectionReason
) {}
