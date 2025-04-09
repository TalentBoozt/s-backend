package com.talentboozt.s_backend.Controller.SYS_TRACKING;

import com.talentboozt.s_backend.Model.SYS_TRACKING.TrackingEvent;
import com.talentboozt.s_backend.Repository.SYS_TRACKING.TrackingEventRepository;
import com.talentboozt.s_backend.Service.SYS_TRACKING.GeoIPService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/event")
public class TrackingController {

    private final TrackingEventRepository eventRepository;

    private final GeoIPService geoIPService;

    public TrackingController(TrackingEventRepository eventRepository, GeoIPService geoIPService) {
        this.eventRepository = eventRepository;
        this.geoIPService = geoIPService;
    }

    @PostMapping("/track")
    public void trackEvent(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        TrackingEvent event = new TrackingEvent();

        // Basic fields
        event.setTrackingId((String) payload.get("tracking_id"));
        event.setEventType((String) payload.get("event_type"));
        event.setUrl((String) payload.get("url"));
        event.setReferrer((String) payload.get("referrer"));
        event.setSessionId((String) payload.get("session_id"));
        event.setUserId((String) payload.get("user_id"));
        event.setScreenResolution((String) payload.get("screen_resolution"));
        event.setBrowser((String) payload.get("browser"));
        event.setLanguage((String) payload.get("language"));
        event.setTimestamp(Instant.now());

        // Geo
        event.setIp(request.getRemoteAddr());
        event = geoIPService.enrichWithGeoIP(event);

        // Click
        event.setElementId((String) payload.get("element_id"));
        event.setElementText((String) payload.get("element_text"));
        event.setElementType((String) payload.get("element_type"));
        event.setElementClass((String) payload.get("element_class"));
        event.setElementAriaLabel((String) payload.get("element_aria_label"));
        event.setElementRouterLink((String) payload.get("element_router_link"));
        event.setClickX(convertToInt(payload.get("click_x")));
        event.setClickY(convertToInt(payload.get("click_y")));

        // Scroll
        event.setScrollPercent(convertToInt(payload.get("scroll_percent")));

        // Duration
        event.setDurationMs(convertToLong(payload.get("duration_ms")));

        // Performance
        event.setDomLoadTime(convertToLong(payload.get("dom_load_time")));
        event.setFullLoadTime(convertToLong(payload.get("full_load_time")));
        event.setTtfb(convertToLong(payload.get("ttfb")));

        // Error data
        event.setErrorMessage((String) payload.get("message"));
        event.setErrorSource((String) payload.get("source"));
        event.setErrorLine(convertToInt(payload.get("lineno")));
        event.setErrorColumn(convertToInt(payload.get("colno")));
        event.setRejectionReason(String.valueOf(payload.get("reason")));

        // Store everything else as customData
        Map<String, Object> custom = new HashMap<>(payload);
        custom.keySet().removeAll(Set.of(
                "tracking_id", "event_type", "url", "referrer", "session_id", "user_id",
                "screen_resolution", "browser", "language", "element_id", "element_text",
                "element_type", "element_class", "element_aria_label", "element_router_link",
                "click_x", "click_y", "scroll_percent", "duration_ms", "dom_load_time",
                "full_load_time", "ttfb", "message", "source", "lineno", "colno", "reason"
        ));
        event.setCustomData(custom.isEmpty() ? null : custom);

        eventRepository.save(event);
    }

    private Integer convertToInt(Object val) {
        if (val instanceof Number) return ((Number) val).intValue();
        try { return val != null ? Integer.parseInt(val.toString()) : null; }
        catch (Exception e) { return null; }
    }

    private Long convertToLong(Object val) {
        if (val instanceof Number) return ((Number) val).longValue();
        try { return val != null ? Long.parseLong(val.toString()) : null; }
        catch (Exception e) { return null; }
    }
}
