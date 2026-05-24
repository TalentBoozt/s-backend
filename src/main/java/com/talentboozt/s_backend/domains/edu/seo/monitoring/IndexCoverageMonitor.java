package com.talentboozt.s_backend.domains.edu.seo.monitoring;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

/**
 * URL Index Coverage Monitor.
 * Catalogues the distribution of indexable versus excluded listings to monitor
 * crawl budget allocation.
 */
@Service
public class IndexCoverageMonitor {

    /**
     * Aggregates coverage distributions.
     */
    public Map<String, Integer> compileCoverageMetrics(int totalUrls, int indexableUrls) {
        Map<String, Integer> metrics = new HashMap<>();
        metrics.put("totalMapped", totalUrls);
        metrics.put("indexable", indexableUrls);
        metrics.put("excluded", totalUrls - indexableUrls);
        return metrics;
    }
}
