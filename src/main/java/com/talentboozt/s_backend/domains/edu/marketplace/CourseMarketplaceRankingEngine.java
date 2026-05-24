package com.talentboozt.s_backend.domains.edu.marketplace;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

/**
 * Course Marketplace Directory Ranking Engine.
 * Compiles dynamic course views, course watch times, and active enrollment targets
 * to order search catalog rankings.
 */
@Service
public class CourseMarketplaceRankingEngine {

    /**
     * Determines high-demand courses for directory spotlights.
     */
    public Map<String, Object> calculateCourseMarketplaceRank(String courseTitle, double watchTimeMinutes, int enrollmentsCount) {
        double demandMultiplier = enrollmentsCount > 500 ? 1.5 : 1.0;
        double score = (watchTimeMinutes * 0.01) + (enrollmentsCount * 0.5) * demandMultiplier;
        
        Map<String, Object> rankMap = new HashMap<>();
        rankMap.put("course", courseTitle);
        rankMap.put("rankingScore", score);
        rankMap.put("demandStatus", score > 1000 ? "HIGH_DEMAND_SPOTLIGHT" : "STEADY_GROWTH");
        
        return rankMap;
    }
}
