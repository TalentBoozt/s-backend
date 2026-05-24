package com.talentboozt.s_backend.domains.edu.seo.analytics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Organic SEO Performance Sync Scheduler.
 * Coordinates automated night-cron calls to pull Google Search Console and Bing Webmaster analytics.
 */
@Component
public class SeoAnalyticsScheduler {

    @Autowired
    private GoogleSearchConsoleService searchConsoleService;

    @Autowired
    private BingWebmasterService bingService;

    /**
     * Executes daily synchronizations.
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void executeSearchEnginesSync() {
        System.out.println("[SEO Scheduler] Starting scheduled search engines analytics synchronization...");
        searchConsoleService.syncSearchConsoleMetrics();
        bingService.syncBingMetrics();
        System.out.println("[SEO Scheduler] Analytics synchronization cycle completed.");
    }
}
