package com.talentboozt.s_backend.domains.edu.career;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Career Gap Evaluator.
 * Highlights missing subjects and recommends dynamic study roadmaps based on job targets.
 */
@Service
public class CareerGapAnalyzer {

    /**
     * Subtracts existing profiles from target qualifications to find the skill gaps.
     */
    public Map<String, Object> analyzeCareerGap(List<String> currentSkillsList, String targetCareerTrack) {
        Map<String, Object> gapReport = new HashMap<>();
        gapReport.put("targetCareer", targetCareerTrack);

        List<String> targetRequirements = new ArrayList<>();
        String normalizedTrack = (targetCareerTrack != null) ? targetCareerTrack.toLowerCase() : "";
        
        if (normalizedTrack.contains("frontend") || normalizedTrack.contains("developer")) {
            targetRequirements.addAll(List.of("HTML/CSS", "JavaScript", "React", "Git", "REST APIs"));
        } else if (normalizedTrack.contains("designer") || normalizedTrack.contains("creative")) {
            targetRequirements.addAll(List.of("Canva", "Figma", "Branding", "Social Media Design"));
        } else {
            targetRequirements.addAll(List.of("Digital Literacy", "Freelancing"));
        }

        List<String> missingSkills = new ArrayList<>();
        for (String skill : targetRequirements) {
            if (currentSkillsList == null || !currentSkillsList.contains(skill)) {
                missingSkills.add(skill);
            }
        }

        gapReport.put("missingSkills", missingSkills);
        double gapRatio = (double) missingSkills.size() / targetRequirements.size();
        gapReport.put("gapPercentage", gapRatio * 100);
        return gapReport;
    }
}
