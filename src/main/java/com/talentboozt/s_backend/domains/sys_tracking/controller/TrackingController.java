package com.talentboozt.s_backend.domains.sys_tracking.controller;

import com.talentboozt.s_backend.domains.sys_tracking.dto.monitor.BatchEventRequest;
import com.talentboozt.s_backend.domains.sys_tracking.model.TrackingEvent;
import com.talentboozt.s_backend.domains.sys_tracking.repository.mongodb.TrackingEventRepository;
import com.talentboozt.s_backend.domains.sys_tracking.service.GeoIPService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Enhanced Tracking Controller for v2.0
 * Supports batch events, all new event types, and improved data handling
 */
@Slf4j
@RestController
@RequestMapping("/api/event")
public class TrackingController {

    @Autowired
    private TrackingEventRepository eventRepository;

    @Autowired
    private GeoIPService geoIPService;

    /**
     * Track single event (backward compatible)
     */
    @PostMapping("/track")
    public ResponseEntity<Map<String, String>> trackEvent(
            @RequestBody Map<String, Object> payload,
            HttpServletRequest request) {
        
        try {
            TrackingEvent event = mapPayloadToEvent(payload, request);
            Objects.requireNonNull(event, "Event cannot be null");
            eventRepository.save(event);
            
            return ResponseEntity.ok(Map.of("status", "success", "id", event.getId()));
        } catch (Exception e) {
            log.error("Error tracking event", e);
            return ResponseEntity.ok(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    /**
     * Track batch of events (new in v2.0)
     */
    @PostMapping("/track/batch")
    public ResponseEntity<Map<String, Object>> trackBatchEvents(
            @RequestBody BatchEventRequest batchRequest,
            HttpServletRequest request) {
        
        try {
            List<TrackingEvent> events = new ArrayList<>();
            
            for (Map<String, Object> payload : batchRequest.getEvents()) {
                TrackingEvent event = mapPayloadToEvent(payload, request);
                events.add(event);
            }
            
            List<TrackingEvent> saved = eventRepository.saveAll(events);
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "count", saved.size(),
                "ids", saved.stream().map(TrackingEvent::getId).toList()
            ));
        } catch (Exception e) {
            log.error("Error tracking batch events", e);
            return ResponseEntity.ok(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    /**
     * Map payload to TrackingEvent
     */
    private TrackingEvent mapPayloadToEvent(Map<String, Object> payload, HttpServletRequest request) {
        TrackingEvent event = new TrackingEvent();

        // Basic fields
        event.setTrackingId(getString(payload, "tracking_id"));
        event.setEventType(getString(payload, "event_type"));
        event.setUrl(getString(payload, "url"));
        event.setReferrer(getString(payload, "referrer"));
        event.setSessionId(getString(payload, "session_id"));
        event.setUserId(getString(payload, "user_id"));
        event.setTimestamp(Instant.now());

        // Page metadata
        event.setPageTitle(getString(payload, "page_title"));
        event.setPagePath(getString(payload, "page_path"));

        // User environment
        event.setScreenResolution(getString(payload, "screen_resolution"));
        event.setBrowser(getString(payload, "browser"));
        event.setLanguage(getString(payload, "language"));
        event.setViewportWidth(getInteger(payload, "viewport_width"));
        event.setViewportHeight(getInteger(payload, "viewport_height"));

        // Geo/IP enrichment
        event.setIp(request.getRemoteAddr());
        event = geoIPService.enrichWithGeoIP(event);

        // Click details
        event.setElementId(getString(payload, "element_id"));
        event.setElementText(getString(payload, "element_text"));
        event.setElementType(getString(payload, "element_type"));
        event.setElementClass(getString(payload, "element_class"));
        event.setElementAriaLabel(getString(payload, "element_aria_label"));
        event.setElementRouterLink(getString(payload, "element_router_link"));
        event.setClickX(getInteger(payload, "click_x"));
        event.setClickY(getInteger(payload, "click_y"));

        // Scroll tracking
        event.setScrollPercent(getInteger(payload, "scroll_percent"));
        event.setScrollDepth(getInteger(payload, "scroll_depth"));

        // Time tracking
        event.setDurationMs(getLong(payload, "duration_ms"));
        event.setActiveTimeMs(getLong(payload, "active_time_ms"));

        // Performance metrics
        event.setDomLoadTime(getLong(payload, "dom_load_time"));
        event.setFullLoadTime(getLong(payload, "full_load_time"));
        event.setTtfb(getLong(payload, "ttfb"));
        event.setDnsTime(getLong(payload, "dns_time"));
        event.setTcpTime(getLong(payload, "tcp_time"));
        event.setDownloadTime(getLong(payload, "download_time"));

        // Web Vitals
        event.setLcpValue(getLong(payload, "value")); // For web_vital_lcp events
        event.setFidValue(getLong(payload, "value")); // For web_vital_fid events

        // Network info
        event.setDownlink(getInteger(payload, "downlink"));
        event.setEffectiveType(getString(payload, "effectiveType"));
        event.setRtt(getInteger(payload, "rtt"));
        event.setSaveData(getBoolean(payload, "saveData"));

        // Error tracking
        event.setErrorMessage(getString(payload, "message"));
        event.setErrorSource(getString(payload, "source"));
        event.setErrorLine(getInteger(payload, "lineno"));
        event.setErrorColumn(getInteger(payload, "colno"));
        event.setErrorStack(getString(payload, "stack"));
        event.setRejectionReason(getString(payload, "reason"));

        // === NEW v2.0 FEATURES ===

        // A/B Testing
        event.setExperimentId(getString(payload, "experiment_id"));
        event.setVariant(getString(payload, "variant"));
        event.setConversionType(getString(payload, "conversion_type"));
        event.setConversionValue(getDouble(payload, "value"));

        // Funnel Tracking
        event.setFunnelId(getString(payload, "funnel_id"));
        event.setStepName(getString(payload, "step_name"));
        event.setStepIndex(getInteger(payload, "step_index"));
        event.setTotalSteps(getInteger(payload, "total_steps"));

        // Form Analytics
        event.setFormId(getString(payload, "form_id"));
        event.setFieldName(getString(payload, "field_name"));
        event.setFieldType(getString(payload, "field_type"));
        event.setFieldsInteracted(getInteger(payload, "fields_interacted"));
        event.setTotalFields(getInteger(payload, "total_fields"));
        event.setTimeSpentMs(getLong(payload, "time_spent_ms"));
        event.setValidationMessage(getString(payload, "validation_message"));

        // Rage Clicks
        event.setClickCount(getInteger(payload, "click_count"));
        event.setAreaX(getInteger(payload, "area_x"));
        event.setAreaY(getInteger(payload, "area_y"));

        // Heatmap Data
        if (payload.containsKey("path")) {
            // Convert path array to JSON string for storage
            event.setHeatmapPath(payload.get("path").toString());
        }

        // Attention Tracking
        event.setAttentionDurationMs(getLong(payload, "duration_ms"));
        event.setViewportPercentage(getInteger(payload, "viewport_percentage"));

        // Timezone
        event.setTimezone(getString(payload, "timezone_name"));
        event.setTimezoneOffset(getInteger(payload, "timezone_offset"));

        // Store unrecognized fields as custom data
        Map<String, Object> custom = new HashMap<>(payload);
        custom.keySet().removeAll(getKnownFields());
        event.setCustomData(custom.isEmpty() ? null : custom);

        // Set TTL (7 days default, configurable)
        event.setExpiresAt(Instant.now().plusSeconds(60 * 60 * 24 * 7));

        return event;
    }

    // Helper methods for type conversion
    private String getString(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null ? val.toString() : null;
    }

    private Integer getInteger(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val instanceof Number) return ((Number) val).intValue();
        try { return val != null ? Integer.parseInt(val.toString()) : null; }
        catch (Exception e) { return null; }
    }

    private Long getLong(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val instanceof Number) return ((Number) val).longValue();
        try { return val != null ? Long.parseLong(val.toString()) : null; }
        catch (Exception e) { return null; }
    }

    private Double getDouble(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val instanceof Number) return ((Number) val).doubleValue();
        try { return val != null ? Double.parseDouble(val.toString()) : null; }
        catch (Exception e) { return null; }
    }

    private Boolean getBoolean(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val instanceof Boolean) return (Boolean) val;
        return val != null && Boolean.parseBoolean(val.toString());
    }

    /**
     * List of all known field names to exclude from customData
     */
    private Set<String> getKnownFields() {
        return Set.of(
            // Basic
            "tracking_id", "event_type", "url", "referrer", "session_id", "user_id",
            "page_title", "page_path",
            // Environment
            "screen_resolution", "browser", "language", "viewport_width", "viewport_height",
            // Click
            "element_id", "element_text", "element_type", "element_class",
            "element_aria_label", "element_router_link", "click_x", "click_y",
            // Scroll
            "scroll_percent", "scroll_depth",
            // Time
            "duration_ms", "active_time_ms",
            // Performance
            "dom_load_time", "full_load_time", "ttfb", "dns_time", "tcp_time", "download_time",
            // Web Vitals
            "value",
            // Network
            "downlink", "effectiveType", "rtt", "saveData",
            // Errors
            "message", "source", "lineno", "colno", "stack", "reason",
            // Experiments
            "experiment_id", "variant", "conversion_type",
            // Funnels
            "funnel_id", "step_name", "step_index", "total_steps",
            // Forms
            "form_id", "field_name", "field_type", "fields_interacted",
            "total_fields", "time_spent_ms", "validation_message",
            // Rage Clicks
            "click_count", "area_x", "area_y",
            // Heatmap
            "path",
            // Attention
            "viewport_percentage",
            // Timezone
            "timezone_name", "timezone_offset"
        );
    }
}
