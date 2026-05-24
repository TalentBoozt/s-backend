package com.talentboozt.s_backend.domains.edu.seo.intelligence;

import org.springframework.stereotype.Service;

/**
 * Autonomous Search Intent Classifier.
 * Classifies query inputs into exact search intent categories to customize
 * visual CTA structures and dynamic user responses.
 */
@Service
public class SearchIntentClassifier {

    /**
     * Determines user intent based on keyword properties.
     */
    public String classifyIntent(String keyword) {
        if (keyword == null) return "INFORMATIONAL";
        String normalized = keyword.toLowerCase().trim();

        if (normalized.contains("best") || normalized.contains("tutor") || 
            normalized.contains("teacher") || normalized.contains("class") || 
            normalized.contains("fee") || normalized.contains("enroll")) {
            
            if (normalized.contains("colombo") || normalized.contains("kandy") || 
                normalized.contains("gampaha") || normalized.contains("near") || 
                normalized.contains("school")) {
                return "TRANSACTIONAL_LOCAL";
            }
            return "TRANSACTIONAL";
        }
        
        if (normalized.contains("how to") || normalized.contains("solve") || 
            normalized.contains("what is") || normalized.contains("explain") || 
            normalized.contains("definition")) {
            return "INFORMATIONAL";
        }
        
        if (normalized.contains("past papers") || normalized.contains("revision") || 
            normalized.contains("notes") || normalized.contains("syllabus")) {
            return "EXAM_REVISION_FOCUS";
        }
        
        return "INFORMATIONAL";
    }
}
