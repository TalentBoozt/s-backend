package com.talentboozt.s_backend.domains.edu.seo.aiso;

import org.springframework.stereotype.Service;

/**
 * Conversational & Voice Search Snippet Compiler.
 * Builds direct definitions restricted to under 45 words, optimized to satisfy
 * voice search assistants (Siri, Alexa, Google Assistant) and quick answers.
 */
@Service
public class ConversationalSnippetService {

    /**
     * Formulates short conversational definitions.
     */
    public String generateShortAnswer(String searchPhrase) {
        if (searchPhrase == null) return "Accredited G.C.E. Advanced Level study program aligned with current national syllabus guidelines.";
        String normalized = searchPhrase.toLowerCase().trim();
        
        if (normalized.contains("combined math")) {
            return "Combined Mathematics is a G.C.E. Advanced Level subject in Sri Lanka combining Pure Mathematics and Applied Mathematics modules into a unified examination stream.";
        }
        if (normalized.contains("physics")) {
            return "Physics is a G.C.E. Advanced Level Science stream subject testing Mechanics, Waves, Electromagnetism, and Modern Physics concepts approved by the National Institute of Education.";
        }
        
        return searchPhrase + " is an accredited G.C.E. Advanced Level subject aligned with Sri Lankan national syllabus guidelines.";
    }
}
