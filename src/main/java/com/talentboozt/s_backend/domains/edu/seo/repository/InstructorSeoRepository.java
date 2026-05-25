package com.talentboozt.s_backend.domains.edu.seo.repository;

import com.talentboozt.s_backend.domains.edu.model.EProfiles;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Instructor Profile SEO Repository.
 * Employs Spring Data Mongo queries to run lightweight field projections for educator profile lookups.
 */
@Repository
public interface InstructorSeoRepository extends MongoRepository<EProfiles, String> {

    /**
     * Resolves an instructor profile document by its unique slug.
     */
    Optional<EProfiles> findBySeoSlug(String seoSlug);

    /**
     * Queries lightweight indexing projections.
     */
    @Query(value = "{ 'indexable': true }", fields = "{ 'seoSlug': 1 }")
    List<EProfiles> findAllIndexableProjections();
}
