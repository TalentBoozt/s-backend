package com.talentboozt.s_backend.domains.edu.ai.recommendation.application;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class AppSkillRecommendationEngine {

    public List<String> recommendNextSkills(String skillArea) {
        if (skillArea == null) return List.of("Prompt Engineering", "ChatGPT Basics");
        String s = skillArea.toLowerCase().trim();
        
        if (s.contains("canva") || s.contains("figma")) {
            return List.of("Social Media Design", "Thumbnail Design", "Freelance Branding", "TikTok Content Creation");
        }
        if (s.contains("prompt") || s.contains("chatgpt") || s.contains("ai")) {
            return List.of("AI Content Creation", "Autonomous Agents", "Vector Database RAG", "Fine Tuning Basics");
        }
        return List.of("React Development", "Next.js Framework", "API Integration");
    }

    public List<String> recommendNextLessons(List<String> completedLessons, String activeCareerPath) {
        List<String> recommendations = new ArrayList<>();
        if (activeCareerPath == null) return recommendations;
        
        String normalizedPath = activeCareerPath.toLowerCase();
        if (normalizedPath.contains("frontend") || normalizedPath.contains("developer")) {
            if (completedLessons == null || !completedLessons.contains("html-basics")) {
                recommendations.add("html-basics");
            }
            if (completedLessons == null || !completedLessons.contains("css-flexbox")) {
                recommendations.add("css-flexbox");
            }
            recommendations.add("react-state-hooks");
        } else {
            recommendations.add("intro-to-prompt-engineering");
            recommendations.add("advanced-chatgpt-hacks");
        }
        return recommendations;
    }
}
