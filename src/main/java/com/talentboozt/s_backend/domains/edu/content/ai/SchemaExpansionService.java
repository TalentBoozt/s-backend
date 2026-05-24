package com.talentboozt.s_backend.domains.edu.content.ai;

import org.springframework.stereotype.Service;
import java.util.*;

/**
 * Advanced Schema Expansion Engine.
 * Constructs deep metadata blueprints (HowTo guides, structured QAPage sets)
 * conforming to Schema.org standards.
 */
@Service
public class SchemaExpansionService {

    /**
     * Compiles dynamic HowTo schema guides.
     */
    public Map<String, Object> compileHowToSchema(String title, List<String> instructionSteps) {
        Map<String, Object> schema = new HashMap<>();
        schema.put("@context", "https://schema.org");
        schema.put("@type", "HowTo");
        schema.put("name", title);
        
        List<Map<String, Object>> stepList = new ArrayList<>();
        if (instructionSteps != null) {
            for (int i = 0; i < instructionSteps.size(); i++) {
                Map<String, Object> step = new HashMap<>();
                step.put("@type", "HowToStep");
                step.put("position", i + 1);
                step.put("text", instructionSteps.get(i));
                stepList.add(step);
            }
        }
        schema.put("step", stepList);
        return schema;
    }

    /**
     * Compiles detailed QAPage schemas mapping voice answers.
     */
    public Map<String, Object> compileQAPageSchema(List<Map<String, String>> qaItems) {
        Map<String, Object> schema = new HashMap<>();
        schema.put("@context", "https://schema.org");
        schema.put("@type", "QAPage");

        List<Map<String, Object>> questionEntities = new ArrayList<>();
        if (qaItems != null) {
            for (Map<String, String> qa : qaItems) {
                Map<String, Object> question = new HashMap<>();
                question.put("@type", "Question");
                question.put("name", qa.get("question"));
                
                Map<String, Object> answerObj = new HashMap<>();
                answerObj.put("@type", "Answer");
                answerObj.put("text", qa.get("answer"));
                question.put("acceptedAnswer", answerObj);
                
                questionEntities.add(question);
            }
        }
        schema.put("mainEntity", questionEntities);
        return schema;
    }
}
