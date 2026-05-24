package com.talentboozt.s_backend.domains.edu.seo.freshness;

import com.talentboozt.s_backend.domains.edu.seo.model.CourseDocument;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Stale Content Detector.
 * Analyzes dynamic timestamps to determine content decay metrics,
 * triggering automatic updates to preserve index rankings.
 */
@Service
public class StaleContentDetector {

    /**
     * Asserts if a course has exceeded maximum freshness thresholds.
     */
    public boolean isContentStale(CourseDocument course, int maxDaysThreshold) {
        if (course.getUpdatedAt() == null) return true;
        
        long diffInMillis = Math.abs(new Date().getTime() - course.getUpdatedAt().getTime());
        long diffInDays = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
        
        return diffInDays > maxDaysThreshold;
    }
}
