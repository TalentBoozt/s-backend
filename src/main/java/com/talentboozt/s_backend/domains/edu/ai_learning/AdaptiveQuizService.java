package com.talentboozt.s_backend.domains.edu.ai_learning;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI Adaptive Quiz Engine.
 * Dynamically scales study question difficulty thresholds (BEGINNER to ADVANCED)
 * depending on student score history.
 */
@Service
public class AdaptiveQuizService {

    /**
     * Forms adaptive quiz questions matching student score performance.
     */
    public Map<String, Object> compileAdaptiveQuiz(String activeSkillArea, int previousScore) {
        Map<String, Object> quizMap = new HashMap<>();
        quizMap.put("skill", activeSkillArea);
        quizMap.put("previousScore", previousScore);

        String calculatedDifficulty = "BEGINNER";
        if (previousScore >= 80) {
            calculatedDifficulty = "ADVANCED";
        } else if (previousScore >= 50) {
            calculatedDifficulty = "INTERMEDIATE";
        }
        
        quizMap.put("assignedDifficulty", calculatedDifficulty);
        quizMap.put("confidencePercentage", previousScore);
        
        List<String> questionsList = List.of(
            "Assert structural properties of " + activeSkillArea + " in modern production code.",
            "Diagnose and optimize performance constraints in a " + activeSkillArea + " setup."
        );
        quizMap.put("questionsList", questionsList);

        return quizMap;
    }
}
