package com.talentboozt.s_backend.domains.lifeplanner.analytics.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.talentboozt.s_backend.domains.lifeplanner.analytics.dto.LPAnalyticsDTO;
import com.talentboozt.s_backend.domains.lifeplanner.analytics.service.LPAnalyticsService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lifeplanner/analytics")
@RequiredArgsConstructor
public class LPAnalyticsController {

    private final LPAnalyticsService analyticsService;

    @GetMapping
    public ResponseEntity<LPAnalyticsDTO> getAnalytics(@RequestHeader("x-user-id") String userId) {
        return ResponseEntity.ok(analyticsService.getAnalytics(userId));
    }
}
