package com.talentboozt.s_backend.domains.edu.career.application;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AppResumeBuilderService {

    public Map<String, Object> compileAtsResume(String candidateName, String targetRole, List<String> skills, List<String> experiences) {
        Map<String, Object> resumeMap = new HashMap<>();
        resumeMap.put("candidateName", candidateName);
        resumeMap.put("targetRole", targetRole);
        resumeMap.put("skillsList", skills);
        resumeMap.put("experienceList", experiences);
        
        String aiGeneratedSummary = "Goal-oriented candidate specializing in " + targetRole + 
                                   ". Academic and project milestones demonstrate expertise in " + String.join(", ", skills) + ".";
        resumeMap.put("aiSummary", aiGeneratedSummary);
        resumeMap.put("atsScore", Math.min(100, skills.size() * 15 + 10));
        
        return resumeMap;
    }

    public Map<String, Object> extractSkillsFromResume(String resumeText) {
        Map<String, Object> extraction = new HashMap<>();
        List<String> parsedSkills = new ArrayList<>();
        if (resumeText != null) {
            String lower = resumeText.toLowerCase();
            if (lower.contains("react")) parsedSkills.add("React");
            if (lower.contains("java")) parsedSkills.add("Java");
            if (lower.contains("mongodb")) parsedSkills.add("MongoDB");
        }
        
        extraction.put("extractedSkills", parsedSkills);
        extraction.put("readinessScore", parsedSkills.size() * 30 + 10);
        return extraction;
    }
}
