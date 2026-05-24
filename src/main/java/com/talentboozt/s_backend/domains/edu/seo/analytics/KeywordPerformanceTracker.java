package com.talentboozt.s_backend.domains.edu.seo.analytics;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Keyword Organic Performance Tracker Repository.
 */
@Repository
public interface KeywordPerformanceTracker extends MongoRepository<SeoKeywordMetricsDocument, String> {
    
    /**
     * Resolves organic keyword metrics.
     */
    Optional<SeoKeywordMetricsDocument> findByKeyword(String keyword);
}
