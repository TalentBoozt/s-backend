package com.talentboozt.s_backend.domains.edu.seo.repository;

import com.talentboozt.s_backend.domains.edu.seo.model.CourseDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Course SEO Repository.
 * Employs Spring Data Mongo queries to run lightweight field projections
 * for performant sitemaps generation.
 */
@Repository
public interface CourseSeoRepository extends MongoRepository<CourseDocument, String> {

    /**
     * Resolves a course document by its unique slug identifier.
     */
    Optional<CourseDocument> findBySeoSlug(String seoSlug);

    /**
     * Queries lightweight indexing projections to conserve server memory during sitemap compiles.
     */
    @Query(value = "{ 'indexable': true }", fields = "{ 'seoSlug': 1, 'updatedAt': 1, 'localizedLangGroupId': 1 }")
    List<CourseDocument> findAllIndexableProjections();
}
