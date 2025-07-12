package com.talentboozt.s_backend.domains.sys_tracking.dto.monitor;

public record SessionEvent(
        String timestamp,
        String eventType,
        String url,
        String elementText
) {}
