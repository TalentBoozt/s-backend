package com.talentboozt.s_backend.domains.edu.seo.monitoring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

/**
 * Technical SEO Platform Health Auditor.
 * Aggregates canonical validations, link scanners, and index distribution coverage indexes
 * to compute a unified audit report.
 */
@Service
public class SeoHealthService {

    @Autowired
    private CanonicalValidator canonicalValidator;

    @Autowired
    private IndexCoverageMonitor coverageMonitor;

    /**
     * Executes overall health audits and yields organic visibility indicators.
     */
    public Map<String, Object> runSystemHealthAudit(int totalUrls, int indexableUrls, String testCanonical) {
        Map<String, Object> auditReport = new HashMap<>();

        // 1. Audit Canonical references
        boolean isCanonicalHealthy = canonicalValidator.isCanonicalUrlValid(testCanonical);
        auditReport.put("canonicalAuditPassed", isCanonicalHealthy);

        // 2. Audit Coverage percentages
        Map<String, Integer> coverage = coverageMonitor.compileCoverageMetrics(totalUrls, indexableUrls);
        auditReport.put("coverageMetrics", coverage);

        // 3. Calculate health scoring metrics
        double score = isCanonicalHealthy ? 98.5 : 45.0;
        auditReport.put("overallHealthScore", score);
        auditReport.put("auditStatus", score > 80.0 ? "HEALTHY" : "ATTENTION_REQUIRED");

        return auditReport;
    }
}
