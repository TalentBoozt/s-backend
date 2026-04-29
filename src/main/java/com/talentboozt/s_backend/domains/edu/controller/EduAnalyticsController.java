package com.talentboozt.s_backend.domains.edu.controller;

import com.talentboozt.s_backend.domains.edu.dto.analytics.CreatorAnalyticsDTO;
import com.talentboozt.s_backend.domains.edu.dto.analytics.LearnerAnalyticsDTO;
import com.talentboozt.s_backend.domains.edu.service.EduAnalyticsDataService;
import com.talentboozt.s_backend.domains.edu.exception.EduAccessDeniedException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/edu/analytics")
public class EduAnalyticsController {

    private final EduAnalyticsDataService analyticsDataService;
    private final com.talentboozt.s_backend.domains.subscription.service.FeatureFlagService featureFlagService;
    private final com.talentboozt.s_backend.domains.edu.service.EduAnalyticsEventService eventService;
    private final com.talentboozt.s_backend.domains.edu.service.EduAnalyticsAggregationService aggregationService;

    public EduAnalyticsController(EduAnalyticsDataService analyticsDataService, 
                                com.talentboozt.s_backend.domains.subscription.service.FeatureFlagService featureFlagService,
                                com.talentboozt.s_backend.domains.edu.service.EduAnalyticsEventService eventService,
                                com.talentboozt.s_backend.domains.edu.service.EduAnalyticsAggregationService aggregationService) {
        this.analyticsDataService = analyticsDataService;
        this.featureFlagService = featureFlagService;
        this.eventService = eventService;
        this.aggregationService = aggregationService;
    }

    @GetMapping("/creator/{creatorId}")
    @PreAuthorize("hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE') or hasAuthority('ENTERPRISE_ADMIN')")
    public ResponseEntity<CreatorAnalyticsDTO> getCreatorAnalytics(@PathVariable String creatorId) {
        if (!featureFlagService.isFeatureEnabled(creatorId, "BASIC_ANALYTICS")) {
            throw new EduAccessDeniedException("Analytics access is not available for your current plan.");
        }
        return ResponseEntity.ok(analyticsDataService.getCreatorAnalytics(creatorId));
    }

    @GetMapping("/learner/{learnerId}")
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE') or hasAuthority('ENTERPRISE_ADMIN')")
    public ResponseEntity<LearnerAnalyticsDTO> getLearnerAnalytics(@PathVariable String learnerId) {
        return ResponseEntity.ok(analyticsDataService.getLearnerAnalytics(learnerId));
    }

    @GetMapping("/creator/{creatorId}/revenue-timeline")
    @PreAuthorize("hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE') or hasAuthority('ENTERPRISE_ADMIN')")
    public ResponseEntity<java.util.Map<String, Double>> getRevenueTimeline(@PathVariable String creatorId) {
        if (!featureFlagService.isFeatureEnabled(creatorId, "ADVANCED_ANALYTICS")) {
            return ResponseEntity.ok(java.util.Map.of());
        }
        return ResponseEntity.ok(analyticsDataService.getRevenueTimeline(creatorId));
    }

    @GetMapping("/course/{courseId}/performance")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<java.util.Map<String, Object>> getCoursePerformance(@PathVariable String courseId) {
        return ResponseEntity.ok(aggregationService.getCoursePerformance(courseId));
    }

    @GetMapping("/creator/{creatorId}/metrics")
    @PreAuthorize("hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE') or hasAuthority('ENTERPRISE_ADMIN')")
    public ResponseEntity<java.util.Map<String, Object>> getCreatorMetrics(@PathVariable String creatorId) {
        if (!featureFlagService.isFeatureEnabled(creatorId, "BASIC_ANALYTICS")) {
            throw new EduAccessDeniedException("Analytics access is not available for your current plan.");
        }
        return ResponseEntity.ok(aggregationService.getCreatorMetrics(creatorId));
    }

    @GetMapping("/platform/metrics")
    @PreAuthorize("hasAuthority('PLATFORM_ADMIN')")
    public ResponseEntity<java.util.Map<String, Object>> getPlatformMetrics() {
        return ResponseEntity.ok(aggregationService.getPlatformMetrics());
    }

    @GetMapping("/events/user/{userId}")
    @PreAuthorize("hasAuthority('ADMIN') or #userId == principal.username")
    public ResponseEntity<java.util.List<com.talentboozt.s_backend.domains.edu.model.EAnalyticsEvents>> getEventsByUser(@PathVariable String userId) {
        return ResponseEntity.ok(eventService.getEventsByUser(userId));
    }

    @GetMapping("/metrics/aggregate")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('PLATFORM_ADMIN')")
    public ResponseEntity<java.util.Map<String, Long>> aggregateMetrics() {
        return ResponseEntity.ok(eventService.aggregateMetrics());
    }
}
