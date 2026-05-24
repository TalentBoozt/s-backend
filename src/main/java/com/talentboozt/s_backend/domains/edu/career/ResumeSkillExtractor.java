package com.talentboozt.s_backend.domains.edu.career;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Resume parsing and profile analysis service.
 * Inspects resume texts to determine present competencies and calculates dynamic job-readiness scores.
 */
@Service
public class ResumeSkillExtractor {

    /**
     * Extracts active skills and evaluates readiness indexes.
     */
    public Map<String, Object> extractSkillsFromResume(String rawResumeText) {
        Map<String, Object> extractionMap = new HashMap<>();
        List<String> detectedSkills = new ArrayList<>();
        
        if (rawResumeText != null) {
            String normalized = rawResumeText.toLowerCase();
            if (normalized.contains("html") || normalized.contains("css")) detectedSkills.add("HTML/CSS");
            if (normalized.contains("javascript") || normalized.contains("js")) detectedSkills.add("JavaScript");
            if (normalized.contains("react")) detectedSkills.add("React");
            if (normalized.contains("canva")) detectedSkills.add("Canva");
            if (normalized.contains("figma")) detectedSkills.add("Figma");
        }

        extractionMap.put("detectedSkills", detectedSkills);
        extractionMap.put("jobReadinessScore", detectedSkills.size() * 20.0);
        return extractionMap;
    }
}
