package com.talentboozt.s_backend.domains.edu.seo.repository;

import com.talentboozt.s_backend.domains.edu.seo.model.CourseDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * AI Feed Repository.
 * Orchestrates paginated lookup streams on MongoDB to feed search engine indexers
 * and RAG indexing scripts.
 */
@Repository
public class SeoFeedRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Streams paginated listings of public, indexable courses.
     */
    public List<CourseDocument> streamCoursesForFeed(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Query query = new Query(Criteria.where("indexable").is(true))
                .with(pageable);
                
        return mongoTemplate.find(query, CourseDocument.class, "courses");
    }
}
