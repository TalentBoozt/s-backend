package com.talentboozt.s_backend.domains.edu.seo.knowledgegraph;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class SemanticRelationshipEngine {

    /**
     * Determines if two topic nodes are semantically related based on keywords.
     */
    public double calculateSemanticCloseness(List<String> keywordsA, List<String> keywordsB) {
        if (keywordsA == null || keywordsB == null || keywordsA.isEmpty() || keywordsB.isEmpty()) {
            return 0.0;
        }
        
        long matches = keywordsA.stream()
                .filter(k -> keywordsB.stream().anyMatch(kb -> kb.equalsIgnoreCase(k)))
                .count();
                
        return (double) matches / Math.max(keywordsA.size(), keywordsB.size());
    }
}
