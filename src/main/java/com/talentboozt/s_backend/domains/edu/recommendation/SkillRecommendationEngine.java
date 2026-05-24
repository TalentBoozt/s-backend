package com.talentboozt.s_backend.domains.edu.recommendation;

import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Skill Recommendation AI Engine.
 * Analyzes active learning focus areas (e.g. Canva) and recommends highly related,
 * secondary career skills to increase platform retention.
 */
@Service
public class SkillRecommendationEngine {

    /**
     * Maps secondary target skills relative to what student is currently studying.
     */
    public List<String> recommendNextSkills(String activeSkillFocus) {
        if (activeSkillFocus == null) return List.of("General Digital Literacy", "Freelancing Fundamentals");
        String normalized = activeSkillFocus.toLowerCase().trim();
        
        if (normalized.contains("canva") || normalized.contains("figma")) {
            return List.of("Social Media Design", "Thumbnail Design", "Freelance Branding", "TikTok Content Creation");
        }
        if (normalized.contains("javascript") || normalized.contains("html") || normalized.contains("css")) {
            return List.of("React Development", "REST APIs", "Git Version Control", "Cloud Deployment");
        }
        if (normalized.contains("prompt") || normalized.contains("chatgpt") || normalized.contains("ai")) {
            return List.of("AI Content Creation", "Autonomous Agents", "Vector Database RAG", "Fine Tuning Basics");
        }

        return List.of("General Digital Literacy", "Freelancing Fundamentals");
    }
}
