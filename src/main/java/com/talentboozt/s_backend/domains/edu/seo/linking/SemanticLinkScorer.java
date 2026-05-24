package com.talentboozt.s_backend.domains.edu.seo.linking;

import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Semantic Link Match Scorer.
 * Uses Jaccard Similarity coefficients across target tags to scoring link candidates,
 * preventing link decay across mismatched subjects.
 */
@Service
public class SemanticLinkScorer {

    /**
     * Determines a similarity value between source and target key sets.
     */
    public double calculateRelevanceScore(List<String> sourceKeywords, List<String> targetKeywords) {
        if (sourceKeywords == null || targetKeywords == null || sourceKeywords.isEmpty() || targetKeywords.isEmpty()) {
            return 0.0;
        }

        Set<String> sourceSet = new HashSet<>();
        for (String kw : sourceKeywords) {
            sourceSet.add(kw.toLowerCase().trim());
        }

        int matches = 0;
        for (String kw : targetKeywords) {
            if (sourceSet.contains(kw.toLowerCase().trim())) {
                matches++;
            }
        }

        // Return Jaccard Index score [0.0 - 1.0]
        return (double) matches / (sourceKeywords.size() + targetKeywords.size() - matches);
    }
}
