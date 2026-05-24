package com.talentboozt.s_backend.domains.edu.ai_learning;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * AI Personalized Learning and Velocity Engine.
 * Suggests lesson recommendations based on career roadmaps and evaluates learning velocity parameters.
 */
@Service
public class PersonalizedLearningEngine {

    /**
     * Maps targeted lesson suggestions matching completed structures and job expectations.
     */
    public List<String> recommendNextLessons(List<String> completedLessons, String activeCareerPath) {
        List<String> recommendationList = new ArrayList<>();
        if (activeCareerPath == null) return recommendationList;
        
        String normalizedPath = activeCareerPath.toLowerCase();

        if (normalizedPath.contains("frontend") || normalizedPath.contains("developer")) {
            if (completedLessons == null || !completedLessons.contains("html-basics")) {
                recommendationList.add("html-basics");
            }
            if (completedLessons == null || !completedLessons.contains("css-flexbox")) {
                recommendationList.add("css-flexbox");
            }
            recommendationList.add("react-state-hooks");
        } else {
            recommendationList.add("intro-to-prompt-engineering");
            recommendationList.add("advanced-chatgpt-hacks");
        }

        return recommendationList;
    }

    /**
     * Evaluates speed ratios (lessons completed per active day).
     */
    public double calculateLearningVelocity(int activeDays, int lessonsCompleted) {
        if (activeDays <= 0) return 0.0;
        return (double) lessonsCompleted / activeDays;
    }
}
