package com.talentboozt.s_backend.domains.analytics.controller;

import com.talentboozt.s_backend.domains.analytics.service.AnalyticsPrecomputationService;
import com.talentboozt.s_backend.domains.analytics.service.AnalyticsQueryService;
import com.talentboozt.s_backend.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/finance/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    private final AnalyticsQueryService queryService;
    private final AnalyticsPrecomputationService precomputationService;

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAnalytics(
            @RequestHeader("X-Organization-Id") String organizationId,
            @RequestParam String projectId,
            @RequestParam(required = false, defaultValue = "base") String scenarioId,
            @RequestParam String metric,
            @RequestParam(required = false, defaultValue = "MONTH") String groupBy) {
        
        return ResponseEntity.ok(ApiResponse.success(queryService.getMetricData(organizationId, projectId, scenarioId, metric, groupBy)));
    }

    @PostMapping("/precompute")
    public ResponseEntity<ApiResponse<Void>> triggerPrecomputation(
            @RequestHeader("X-Organization-Id") String organizationId,
            @RequestParam String projectId,
            @RequestParam(required = false, defaultValue = "base") String scenarioId) {
        
        precomputationService.precomputeAll(organizationId, projectId, scenarioId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
