package com.talentboozt.s_backend.domains.edu.seo.intelligence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * Technical Keyword Opportunity Finder.
 * Computes opportunity matrices comparing search volumes against keyword difficulties,
 * saving entries in "seo_keyword_opportunities" to prioritize local page creation.
 */
@Service
public class KeywordOpportunityEngine {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Evaluates opportunity scores and saves record entries to MongoDB.
     */
    public Map<String, Object> evaluateKeywordOpportunity(String keyword, int monthlyVolume, int difficulty) {
        double baselineScore = (double) (100 - difficulty) * monthlyVolume / 10000.0;
        double opportunityScore = Math.min(1.0, baselineScore);
        
        Map<String, Object> opportunityMap = new HashMap<>();
        opportunityMap.put("keyword", keyword);
        opportunityMap.put("volume", monthlyVolume);
        opportunityMap.put("difficulty", difficulty);
        opportunityMap.put("opportunityScore", opportunityScore);
        opportunityMap.put("scoredAt", new Date());

        mongoTemplate.save(opportunityMap, "seo_keyword_opportunities");
        return opportunityMap;
    }
}
