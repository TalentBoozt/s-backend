package com.talentboozt.s_backend.Model.SYS_TRACKING;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Data
@Document(collection = "events")
public class TrackingEvent {
    @Id
    private String id;

    private String trackingId;      // SaaS platform
    private String eventType;       // page_view, click, scroll, etc.
    private String url;
    private String referrer;

    private String sessionId;
    private String userId;

    private String screenResolution;
    private String browser;
    private String language;
    private String ip;
    private String country;
    private String region;
    private String city;

    private Instant timestamp;

    // Click details
    private String elementId;
    private String elementText;
    private String elementType;
    private String elementClass;
    private String elementAriaLabel;
    private String elementRouterLink;
    private Integer clickX;
    private Integer clickY;

    // Scroll
    private Integer scrollPercent;

    // Page time
    private Long durationMs;

    // Performance
    private Long domLoadTime;
    private Long fullLoadTime;
    private Long ttfb;

    // Errors
    private String errorMessage;
    private String errorSource;
    private Integer errorLine;
    private Integer errorColumn;
    private String rejectionReason;

    // For custom events or future-proofing
    private Map<String, Object> customData;
}
