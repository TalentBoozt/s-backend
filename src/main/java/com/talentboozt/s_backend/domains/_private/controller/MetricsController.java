package com.talentboozt.s_backend.domains._private.controller;

import com.talentboozt.s_backend.domains._private.dto.PagedResponse;
import com.talentboozt.s_backend.shared.security.service.GeoLocationService;
import com.talentboozt.s_backend.domains._private.service.UserActivityService;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v2/metrics")
public class MetricsController {

    @Autowired
    private GeoLocationService geoLocationService;

    @Autowired
    private UserActivityService userActivityService;

    private final MeterRegistry meterRegistry;

    public MetricsController(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @GetMapping("/task-executor")
    public Map<String, Object> getTaskExecutorMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        try {
            metrics.put("active", meterRegistry.get("taskExecutor.active").gauge().value());
            metrics.put("queueSize", meterRegistry.get("taskExecutor.queued").gauge().value());
            metrics.put("poolSize", meterRegistry.get("taskExecutor.pool.size").gauge().value());
        } catch (Exception e) {
            metrics.put("active", 0);
            metrics.put("queueSize", 0);
            metrics.put("poolSize", 0);
        }
        return metrics;
    }

    @GetMapping("/user-activity")
    public Map<String, Object> getUserActivityMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("activeUsers", meterRegistry.counter("user.active.count").count());
        metrics.put("averageSessionDuration", meterRegistry.timer("user.session.duration").mean(TimeUnit.SECONDS));
        return metrics;
    }

    @GetMapping(value = "/geolocation", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> getGeoLocation(@RequestParam String ipAddress) {
        try {
            Map<String, String> location = geoLocationService.getGeoLocation(ipAddress);
            return ResponseEntity.ok(location);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping(value = "/user-activities", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PagedResponse<Map<String, String>>> getUserActivity(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        List<Map<String, String>> activity = userActivityService.getUserActivities(page, size);
        return ResponseEntity.ok(new PagedResponse<>(activity, userActivityService.getTotalElements()));
    }

    @GetMapping("/activity-trends")
    public ResponseEntity<Map<String, Long>> getActivityTrends(
            @RequestParam(defaultValue = "hourly") String interval,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        Map<String, Long> trends = userActivityService.getActivityOverTime(interval, page, size);
        return ResponseEntity.ok(trends);
    }

    @DeleteMapping("/delete/user-activities")
    public ResponseEntity<String> clearUserActivities() {
        return userActivityService.clearUserActivities();
    }

    @GetMapping("/active-users")
    public ResponseEntity<Long> getActiveUsers() {
        long activeUsers = userActivityService.getActiveUserCount();
        return ResponseEntity.ok(activeUsers);
    }
}

