package com.talentboozt.s_backend.domains.edu.seo.linking;

import org.springframework.stereotype.Service;

/**
 * Anchor Text Compilation Engine.
 * Dynamically outputs contextual and keyword-rich anchor text structures to optimize
 * link relevance indicators for search crawl engines.
 */
@Service
public class AnchorTextGenerator {

    /**
     * Compiles localized educational anchors.
     */
    public String generateAnchorText(String subject, String targetType) {
        if (subject == null) return "Explore our tuition classes";
        String normalizedSubject = subject.toLowerCase().trim();
        
        if ("course".equalsIgnoreCase(targetType)) {
            return "Enroll in expert " + normalizedSubject + " revision online classes";
        }
        if ("teacher".equalsIgnoreCase(targetType)) {
            return "Learn from top G.C.E. A/L " + normalizedSubject + " certified lecturers";
        }
        if ("notes".equalsIgnoreCase(targetType)) {
            return "Download free A/L " + normalizedSubject + " study guides and papers";
        }
        
        return "Explore certified " + normalizedSubject + " tuition programs in Sri Lanka";
    }
}
