package com.talentboozt.s_backend.domains.edu.seo.budget;

import org.springframework.stereotype.Service;

/**
 * Crawl Budget Optimization Priority Engine.
 * Dynamically computes crawl prioritization indices (0.1 to 1.0) to channel
 * search engine bots toward high-converting revision listings.
 */
@Service
public class CrawlPriorityEngine {

    /**
     * Determines prioritization levels using page metadata.
     */
    public double calculateCrawlPriority(int monthlyPageViews, boolean isFreshlyUpdated, boolean isThinContent) {
        if (isThinContent) return 0.1;
        
        double priorityScore = 0.5;
        if (monthlyPageViews > 1000) priorityScore += 0.2;
        if (isFreshlyUpdated) priorityScore += 0.3;
        
        return Math.min(1.0, priorityScore);
    }
}
