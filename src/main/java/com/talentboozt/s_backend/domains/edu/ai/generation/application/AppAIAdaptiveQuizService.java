package com.talentboozt.s_backend.domains.edu.ai.generation.application;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AppAIAdaptiveQuizService {

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
