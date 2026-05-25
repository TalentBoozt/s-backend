package com.talentboozt.s_backend.domains.edu.seo.freshness;

import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.seo.repository.CourseSeoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * Auto Content Refresh Scheduler.
 * Coordinates automated night-cron runs to identify decayed course listings,
 * triggering dynamic metadata refreshes to maintain optimal search rankings.
 */
@Component
public class AutoRefreshScheduler {

    @Autowired
    private CourseSeoRepository courseRepository;

    @Autowired
    private StaleContentDetector detector;

    @Autowired
    private FreshnessScoreService freshnessService;

    /**
     * Periodically runs refresh operations to avoid metadata decay.
     */
    @Scheduled(cron = "0 0 4 * * ?")
    public void autoRefreshStaleContent() {
        System.out.println("[Freshness Scheduler] Starting auto scan of stale dynamic courses...");
        List<ECourses> courses = courseRepository.findAll();

        int count = 0;
        for (ECourses course : courses) {
            if (detector.isContentStale(course, 30)) {
                freshnessService.triggerContentRefresh(course);
                count++;
            }
        }
        System.out.println("[Freshness Scheduler] Content refresh completed. Updated: " + count + " items.");
    }
}
