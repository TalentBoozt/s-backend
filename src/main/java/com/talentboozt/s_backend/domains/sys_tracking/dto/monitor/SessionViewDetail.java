package com.talentboozt.s_backend.domains.sys_tracking.dto.monitor;

import java.util.List;

public record SessionViewDetail(
        String sessionId,
        String userId,
        long duration,
        int eventCount,
        String browser,
        String screenResolution,
        String language,
        String referrer,
        long domLoadTime,
        long fullLoadTime,
        long ttfb,
        List<SessionError> errors,
        List<SessionEvent> events
) {}
