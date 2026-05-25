package com.talentboozt.s_backend.domains.edu.seo.analytics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

/**
 * REST Controller serving analytics aggregation to the AI Visibility Dashboard.
 */
@RestController
@RequestMapping("/api/v1/edu/seo/analytics")
@CrossOrigin(originPatterns = "*")
public class AICrawlerAnalyticsController {

    @Autowired
    private CrawlAnalyticsService analyticsService;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardMetrics() {
        return ResponseEntity.ok(analyticsService.getAnalyticsDashboardMetrics());
    }
}
