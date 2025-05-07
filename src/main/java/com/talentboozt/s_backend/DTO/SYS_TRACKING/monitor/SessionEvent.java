package com.talentboozt.s_backend.DTO.SYS_TRACKING.monitor;

public record SessionEvent(
        String timestamp,
        String eventType,
        String url,
        String elementText
) {}
