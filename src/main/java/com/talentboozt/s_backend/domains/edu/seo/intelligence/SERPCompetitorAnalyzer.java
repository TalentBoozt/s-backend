package com.talentboozt.s_backend.domains.edu.seo.intelligence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * SERP Competitor Auditing Engine.
 * Logs competitor data, title templates, and Schema usages into "seo_serp_competitors"
 * to automate organic optimization feedback loops.
 */
@Service
public class SERPCompetitorAnalyzer {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Records competitor analysis properties inside MongoDB.
     */
    public void analyzeCompetitor(String domain, String keyword, List<String> schemaTypes) {
        Map<String, Object> competitorLog = new HashMap<>();
        competitorLog.put("domain", domain);
        competitorLog.put("keyword", keyword);
        competitorLog.put("schemaTypes", schemaTypes);
        competitorLog.put("analyzedAt", new Date());

        mongoTemplate.save(competitorLog, "seo_serp_competitors");
        System.out.println("[Competitor Analyzer] Logged SERP audit metrics for: " + domain);
    }
}
