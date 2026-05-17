package com.talentboozt.s_backend.domains.jobs.service;

import com.talentboozt.s_backend.domains.jobs.model.JobPostModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JobAiService {

    /**
     * Generates a job description based on title and key requirements.
     */
    public String generateDescription(String title, List<String> skills, String industry) {
        // Placeholder for AI call (OpenAI/Gemini)
        return "AI Generated Description for " + title + " in " + industry + " industry.";
    }

    /**
     * Recommends skills based on job title.
     */
    public List<String> recommendSkills(String title) {
        return List.of("React", "TypeScript", "Node.js", "System Design");
    }

    /**
     * Suggests salary range based on market data.
     */
    public Map<String, Double> suggestSalaryRange(String title, String location) {
        return Map.of("min", 120000.0, "max", 180000.0);
    }
}
