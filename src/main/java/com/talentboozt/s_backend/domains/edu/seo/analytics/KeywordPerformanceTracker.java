package com.talentboozt.s_backend.domains.edu.seo.analytics;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.talentboozt.s_backend.domains.edu.model.ESeoKeywordMetrics;
import java.util.Optional;

/**
 * Keyword Organic Performance Tracker Repository.
 */
@Repository
public interface KeywordPerformanceTracker extends MongoRepository<ESeoKeywordMetrics, String> {
    
    /**
     * Resolves organic keyword metrics.
     */
    Optional<ESeoKeywordMetrics> findByKeyword(String keyword);
}
