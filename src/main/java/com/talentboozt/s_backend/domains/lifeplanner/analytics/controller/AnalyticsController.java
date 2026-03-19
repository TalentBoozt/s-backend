package com.talentboozt.s_backend.domains.lifeplanner.analytics.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.talentboozt.s_backend.domains.lifeplanner.analytics.dto.AnalyticsDTO;
import com.talentboozt.s_backend.domains.lifeplanner.analytics.service.AnalyticsService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lifeplanner/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping
    public ResponseEntity<AnalyticsDTO> getAnalytics(@RequestHeader("x-user-id") String userId) {
        return ResponseEntity.ok(analyticsService.getAnalytics(userId));
    }
}
