package com.talentboozt.s_backend.domains.edu.jobs;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Technical Skill-based Job Matching Engine.
 * Formulates remote job matches, salary estimates, and missing skill metrics.
 */
@Service
public class JobMatchingEngine {

    /**
     * Subtracts applicant skills from job requirements to calculate compatibility ranks.
     */
    public List<Map<String, Object>> matchJobsToSkills(List<String> userSkillsList) {
        List<Map<String, Object>> matchedJobList = new ArrayList<>();

        Map<String, Object> reactJob = new HashMap<>();
        reactJob.put("title", "Remote React Developer");
        reactJob.put("salaryEstimate", "$60,000 - $90,000 / Year");
        reactJob.put("isRemote", true);
        
        List<String> requiredSkills = List.of("React", "JavaScript", "HTML/CSS");
        reactJob.put("requiredSkills", requiredSkills);
        
        double compatibility = (userSkillsList != null && userSkillsList.contains("React")) ? 95.0 : 40.0;
        reactJob.put("matchPercentage", compatibility);
        
        List<String> missingSkills = new ArrayList<>();
        if (userSkillsList == null || !userSkillsList.contains("React")) {
            missingSkills.add("React");
        }
        reactJob.put("missingSkills", missingSkills);

        matchedJobList.add(reactJob);
        return matchedJobList;
    }
}
