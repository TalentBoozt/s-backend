package com.talentboozt.s_backend.domains.sys_tracking.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

/**
 * Enhanced Tracking Event Model for v2.0
 * Supports all new features: A/B testing, funnels, forms, heatmaps, rage clicks
 */
@Data
@Document(collection = "events")
public class TrackingEvent {
    @Id
    private String id;

    // Basic tracking
    private String trackingId;      // SaaS platform identifier
    private String eventType;       // page_view, click, scroll, experiment_assigned, funnel_step, etc.
    private String url;
    private String referrer;
    
    @Indexed
    private String sessionId;
    private String userId;
    
    @Indexed
    private Instant timestamp;

    // User environment
    private String screenResolution;
    private String browser;
    private String language;
    private Integer viewportWidth;
    private Integer viewportHeight;
    
    // Geo/IP data
    private String ip;
    private String country;
    private String region;
    private String city;
    private String countryCode;
    private String isp;
    private Boolean proxy;
    private Boolean suspectedVpn;
    private Boolean suspectedBot;

    // Page metadata
    private String pageTitle;
    private String pagePath;

    // Click details
    private String elementId;
    private String elementText;
    private String elementType;
    private String elementClass;
    private String elementAriaLabel;
    private String elementRouterLink;
    private Integer clickX;
    private Integer clickY;

    // Scroll tracking
    private Integer scrollPercent;
    private Integer scrollDepth;

    // Time tracking
    private Long durationMs;        // Total duration
    private Long activeTimeMs;      // Active time (not idle)

    // Performance metrics
    private Long domLoadTime;
    private Long fullLoadTime;
    private Long ttfb;
    private Long dnsTime;
    private Long tcpTime;
    private Long downloadTime;

    // Web Vitals
    private Long lcpValue;          // Largest Contentful Paint
    private Long fidValue;          // First Input Delay

    // Network info
    private Integer downlink;       // Mbps
    private String effectiveType;   // 4g, 3g, etc.
    private Integer rtt;            // Round trip time in ms
    private Boolean saveData;

    // Error tracking
    private String errorMessage;
    private String errorSource;
    private Integer errorLine;
    private Integer errorColumn;
    private String errorStack;
    private String rejectionReason;

    // === NEW v2.0 FEATURES ===

    // A/B Testing / Experiments
    private String experimentId;
    private String variant;
    private String conversionType;
    private Double conversionValue;

    // Funnel Tracking
    private String funnelId;
    private String stepName;
    private Integer stepIndex;
    private Integer totalSteps;

    // Form Analytics
    private String formId;
    private String fieldName;
    private String fieldType;
    private Integer fieldsInteracted;
    private Integer totalFields;
    private Long timeSpentMs;
    private String validationMessage;

    // Rage Clicks & Dead Clicks
    private Integer clickCount;     // For rage clicks
    private Integer areaX;          // Rage click area
    private Integer areaY;

    // Heatmap Data
    private String heatmapPath;     // Mouse movement path (JSON string)
    
    // Attention Tracking
    private Long attentionDurationMs;
    private Integer viewportPercentage;

    // Timezone
    private String timezone;
    private Integer timezoneOffset;

    // Custom data for extensibility
    private Map<String, Object> customData;

    // TTL for automatic cleanup
    @Indexed(name = "expireAtIndex", expireAfter = "0s")
    private Instant expiresAt;
}
