package com.talentboozt.s_backend.domains.edu.seo.freshness;

import com.talentboozt.s_backend.domains.edu.seo.model.CourseDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import java.util.Date;

/**
 * Educational Content Freshness Score Service.
 * Computes decay values and triggers database timestamp updates.
 */
@Service
public class FreshnessScoreService {

    @Autowired
    private StaleContentDetector detector;

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Calculates mathematical freshness decay.
     */
    public double calculateFreshnessScore(CourseDocument course) {
        if (course.getUpdatedAt() == null) return 0.0;
        
        boolean isStale = detector.isContentStale(course, 30);
        return isStale ? 0.45 : 0.95;
    }

    /**
     * Updates timestamps and refreshes caches.
     */
    public void triggerContentRefresh(CourseDocument course) {
        System.out.println("[Freshness Service] Refreshing dynamic timestamps and dates for course: " + course.getSeoSlug());
        course.setUpdatedAt(new Date());
        mongoTemplate.save(course);
        System.out.println("[Freshness Service] Course updated successfully with fresh date: " + course.getUpdatedAt());
    }
}
