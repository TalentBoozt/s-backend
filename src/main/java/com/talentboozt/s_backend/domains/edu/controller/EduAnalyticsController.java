package com.talentboozt.s_backend.domains.edu.controller;

import com.talentboozt.s_backend.domains.edu.dto.analytics.CreatorAnalyticsDTO;
import com.talentboozt.s_backend.domains.edu.dto.analytics.LearnerAnalyticsDTO;
import com.talentboozt.s_backend.domains.edu.service.EduAnalyticsDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/edu/analytics")
public class EduAnalyticsController {

    private final EduAnalyticsDataService analyticsDataService;

    public EduAnalyticsController(EduAnalyticsDataService analyticsDataService) {
        this.analyticsDataService = analyticsDataService;
    }

    @GetMapping("/creator/{creatorId}")
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR') or hasAuthority('ADMIN')")
    public ResponseEntity<CreatorAnalyticsDTO> getCreatorAnalytics(@PathVariable String creatorId) {
        return ResponseEntity.ok(analyticsDataService.getCreatorAnalytics(creatorId));
    }

    @GetMapping("/learner/{learnerId}")
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR') or hasAuthority('ADMIN')")
    public ResponseEntity<LearnerAnalyticsDTO> getLearnerAnalytics(@PathVariable String learnerId) {
        return ResponseEntity.ok(analyticsDataService.getLearnerAnalytics(learnerId));
    }

    @GetMapping("/creator/{creatorId}/revenue-timeline")
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR') or hasAuthority('ADMIN')")
    public ResponseEntity<java.util.Map<String, Double>> getRevenueTimeline(@PathVariable String creatorId) {
        return ResponseEntity.ok(analyticsDataService.getRevenueTimeline(creatorId));
    }
}
