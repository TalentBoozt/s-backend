package com.talentboozt.s_backend.shared.audit.tracking.dto.monitor;

public record SessionEvent(
        String timestamp,
        String eventType,
        String url,
        String elementText
) {}
