package com.talentboozt.s_backend.domains.edu.seo.analytics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CrawlAnalyticsService {

    @Autowired
    private CrawlVisitLogRepository logRepository;

    /**
     * Saves an incoming crawler log.
     */
    public void recordVisit(String botName, String uri, String userAgent, String ip, long responseTimeMs, int status, String delivery) {
        CrawlVisitLog log = new CrawlVisitLog();
        log.setBotName(botName);
        log.setRequestUri(uri);
        log.setUserAgent(userAgent);
        log.setIpAddress(ip);
        log.setResponseTimeMs(responseTimeMs + "ms");
        log.setHttpStatus(status);
        log.setDeliverySummary(delivery);
        logRepository.save(log);
    }

    /**
     * Retrieves aggregated metrics for the AI Analytics Dashboard.
     */
    public Map<String, Object> getAnalyticsDashboardMetrics() {
        Map<String, Object> dashboard = new HashMap<>();

        List<CrawlVisitLog> allLogs = logRepository.findAll();
        dashboard.put("totalCrawlVisits", allLogs.size());

        // Crawls in the last 24 hours
        Instant past24Hours = Instant.now().minus(24, ChronoUnit.HOURS);
        List<CrawlVisitLog> recentLogs = allLogs.stream()
                .filter(l -> l.getTimestamp().isAfter(past24Hours))
                .toList();
        dashboard.put("crawlVisits24h", recentLogs.size());

        // Bot distribution
        Map<String, Long> botDistribution = allLogs.stream()
                .collect(Collectors.groupingBy(CrawlVisitLog::getBotName, Collectors.counting()));
        dashboard.put("botDistribution", botDistribution);

        // Top crawled URLs
        Map<String, Long> topUrls = allLogs.stream()
                .collect(Collectors.groupingBy(CrawlVisitLog::getRequestUri, Collectors.counting()));
        List<Map<String, Object>> topUrlsSorted = topUrls.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(10)
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("url", entry.getKey());
                    item.put("count", entry.getValue());
                    return item;
                })
                .collect(Collectors.toList());
        dashboard.put("topCrawledUrls", topUrlsSorted);

        // Success vs Fail rate
        long successCount = allLogs.stream().filter(l -> l.getHttpStatus() >= 200 && l.getHttpStatus() < 300).count();
        long failureCount = allLogs.stream().filter(l -> l.getHttpStatus() >= 400).count();
        dashboard.put("successRate", allLogs.isEmpty() ? 100.0 : (successCount * 100.0 / allLogs.size()));
        dashboard.put("failureCount", failureCount);

        return dashboard;
    }
}
