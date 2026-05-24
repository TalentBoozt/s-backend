package com.talentboozt.s_backend.domains.edu.resume;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Technical ATS-Optimized Resume Builder Service.
 * Evaluates candidate summaries, scores keywords, and maps target job readiness.
 */
@Service
public class ResumeBuilderService {

    /**
     * Packages applicant data into a standardized resume format.
     */
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
}
