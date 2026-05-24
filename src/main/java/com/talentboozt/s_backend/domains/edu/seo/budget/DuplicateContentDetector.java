package com.talentboozt.s_backend.domains.edu.seo.budget;

import org.springframework.stereotype.Service;

/**
 * Technical Duplicate Content and Cannibalization Guard.
 * Analyzes dynamic titles and descriptions to flag programmatic overlaps,
 * preventing Google indexing penalties.
 */
@Service
public class DuplicateContentDetector {

    /**
     * Asserts if two pages overlap in search signals.
     */
    public boolean isNearDuplicate(String firstTitle, String secondTitle) {
        if (firstTitle == null || secondTitle == null) return false;
        
        String normalizedA = firstTitle.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
        String normalizedB = secondTitle.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
        
        return normalizedA.equals(normalizedB);
    }
}
