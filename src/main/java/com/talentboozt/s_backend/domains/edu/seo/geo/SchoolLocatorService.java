package com.talentboozt.s_backend.domains.edu.seo.geo;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

/**
 * Local High School Locator Service.
 * Resolves local campus names (e.g., Royal College, Hillwood College) to their administrative
 * districts and coordinates, capturing hyper-local student search intents.
 */
@Service
public class SchoolLocatorService {

    /**
     * Resolves high-school properties corresponding to campus search titles.
     */
    public Map<String, Object> resolveSchoolMetrics(String highSchoolName) {
        Map<String, Object> schoolMetrics = new HashMap<>();
        if (highSchoolName == null) return schoolMetrics;
        
        String normalized = highSchoolName.toLowerCase().trim();

        if (normalized.contains("royal")) {
            schoolMetrics.put("name", "Royal College, Colombo 07");
            schoolMetrics.put("district", "Colombo");
            schoolMetrics.put("latitude", 6.9062);
            schoolMetrics.put("longitude", 79.8606);
        } else if (normalized.contains("kandy girls") || normalized.contains("hillwood")) {
            schoolMetrics.put("name", "Hillwood College, Kandy");
            schoolMetrics.put("district", "Kandy");
            schoolMetrics.put("latitude", 7.2915);
            schoolMetrics.put("longitude", 80.6412);
        } else {
            schoolMetrics.put("name", highSchoolName);
            schoolMetrics.put("district", "General Sri Lanka");
            schoolMetrics.put("latitude", 6.9271);
            schoolMetrics.put("longitude", 79.8612);
        }

        return schoolMetrics;
    }
}
