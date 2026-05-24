package com.talentboozt.s_backend.domains.edu.marketplace;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

/**
 * Technical Instructor Authority Service.
 * Ranks educators based on student course completion ratios and aggregate ratings.
 */
@Service
public class InstructorAuthorityService {

    /**
     * Ranks educators and outputs verified designations.
     */
    public Map<String, Object> evaluateInstructorAuthority(String instructorName, double studentCompletionRate, double ratingScore) {
        double score = (studentCompletionRate * 40.0) + (ratingScore * 10.0 * 0.6);
        double authorityScore = Math.min(100.0, score);
        
        Map<String, Object> authorityMap = new HashMap<>();
        authorityMap.put("instructor", instructorName);
        authorityMap.put("authorityScore", authorityScore);
        authorityMap.put("authorityLevel", authorityScore > 80.0 ? "VERIFIED_EXPERT" : "ACTIVE_TUTOR");
        
        return authorityMap;
    }
}
