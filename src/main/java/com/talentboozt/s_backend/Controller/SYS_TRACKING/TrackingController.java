package com.talentboozt.s_backend.Controller.SYS_TRACKING;

import com.talentboozt.s_backend.Model.SYS_TRACKING.TrackingEvent;
import com.talentboozt.s_backend.Repository.SYS_TRACKING.TrackingEventRepository;
import com.talentboozt.s_backend.Service.SYS_TRACKING.GeoIPService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/track")
public class TrackingController {

    private final TrackingEventRepository eventRepository;

    private final GeoIPService geoIPService;

    public TrackingController(TrackingEventRepository eventRepository, GeoIPService geoIPService) {
        this.eventRepository = eventRepository;
        this.geoIPService = geoIPService;
    }

    @PostMapping
    public void trackEvent(@RequestBody TrackingEvent event, HttpServletRequest request) {
        event.setIp(request.getRemoteAddr());
        event.setTimestamp(Instant.now());

        // Add GeoIP Details
        event = geoIPService.enrichWithGeoIP(event);

        eventRepository.save(event);
    }
}
