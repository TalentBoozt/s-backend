package com.talentboozt.s_backend.domains.edu.ai_learning;

import org.springframework.stereotype.Service;

/**
 * AI Academic and Career Tutor Service.
 * Formulates interactive explanations, conceptual breakdowns, interview questions,
 * and coding challenges to coach multi-domain students.
 */
@Service
public class AiLearningTutorService {

    /**
     * Responds to study prompts using conversational simplification strategies.
     */
    public String generateTutorResponse(String targetConcept, String interactiveMode) {
        if (interactiveMode == null || targetConcept == null) return "Complete structured study streaks to master definitions.";
        String normalizedMode = interactiveMode.toLowerCase().trim();
        
        if (normalizedMode.contains("simplify") || normalizedMode.contains("explain")) {
            return "Simplified Breakdown of " + targetConcept + ": Think of it like a restaurant kitchen. The waiter (API) takes your order (Request) and brings back your food (Response) from the kitchen (Server).";
        }
        if (normalizedMode.contains("interview")) {
            return "Mock Interview prep for " + targetConcept + ": Explain how to optimize " + targetConcept + " for remote scale. Detail edge cache headers and lazy assets.";
        }
        if (normalizedMode.contains("coding") || normalizedMode.contains("exercise")) {
            return "Coding Exercise: Implement a baseline " + targetConcept + " block using modern JavaScript/TypeScript. Assert robust input validation.";
        }

        return "Expert AI Tutor Outline: " + targetConcept + " maps crucial modules inside career roadmaps. Complete study guides to learn more.";
    }
}
