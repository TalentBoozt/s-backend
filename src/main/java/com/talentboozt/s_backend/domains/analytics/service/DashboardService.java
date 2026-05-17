package com.talentboozt.s_backend.domains.analytics.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final RedisTemplate<String, Object> redisTemplate;
    // repositories...

    public Map<String, Object> getRecruiterOverview(String orgId) {
        String cacheKey = "dashboard:overview:" + orgId;
        Map<String, Object> cachedData = (Map<String, Object>) redisTemplate.opsForValue().get(cacheKey);
        
        if (cachedData != null) {
            return cachedData;
        }

        // Calculate fresh metrics (Simulated for now, replace with actual DB queries)
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("activeJobs", 12);
        metrics.put("totalCandidates", 842);
        metrics.put("avgTimeToHire", "18d");
        metrics.put("aiMatchRate", "92%");
        
        // Cache for 15 minutes
        redisTemplate.opsForValue().set(cacheKey, metrics, Duration.ofMinutes(15));
        
        return metrics;
    }
}
