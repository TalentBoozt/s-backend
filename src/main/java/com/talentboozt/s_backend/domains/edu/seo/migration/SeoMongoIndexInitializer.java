package com.talentboozt.s_backend.domains.edu.seo.migration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.stereotype.Component;

/**
 * MongoDB SEO Index Initializer.
 * Dynamically enforces high-speed indexes on startups to optimize bot querying speeds,
 * utilizing sparse uniques and weighted text indices.
 */
@Component
public class SeoMongoIndexInitializer implements CommandLineRunner {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>> INITIALIZING MONGODB SEO INDEX STRATEGY...");

        // 1. Initialize Courses Indices
        if (mongoTemplate.collectionExists("courses")) {
            // Unique Sparse Slug Index for bot mapping
            mongoTemplate.indexOps("courses").ensureIndex(
                new Index().on("seoSlug", Sort.Direction.ASC).unique().sparse()
            );

            // Sparse Lang Group Index for alternate mapping
            mongoTemplate.indexOps("courses").ensureIndex(
                new Index().on("localizedLangGroupId", Sort.Direction.ASC).sparse()
            );

            // Published/Updated Sorting Indices
            mongoTemplate.indexOps("courses").ensureIndex(
                new Index().on("publishedAt", Sort.Direction.DESC)
            );
            mongoTemplate.indexOps("courses").ensureIndex(
                new Index().on("updatedAt", Sort.Direction.DESC)
            );

            // Composite Text Index for high-density keyword searches
            TextIndexDefinition coursesTextIndex = new TextIndexDefinition.TextIndexDefinitionBuilder()
                .onField("seoTitle", 3f)
                .onField("seoDescription", 2f)
                .onField("semanticKeywords", 1f)
                .build();
            mongoTemplate.indexOps("courses").ensureIndex(coursesTextIndex);
        }

        // 2. Initialize Instructor Profiles Indices
        if (mongoTemplate.collectionExists("instructor_profiles")) {
            // Unique Sparse Slug Index for bot mapping
            mongoTemplate.indexOps("instructor_profiles").ensureIndex(
                new Index().on("seoSlug", Sort.Direction.ASC).unique().sparse()
            );

            // Composite Text Index for educator lookup
            TextIndexDefinition profilesTextIndex = new TextIndexDefinition.TextIndexDefinitionBuilder()
                .onField("name", 3f)
                .onField("expertise", 2f)
                .onField("semanticKeywords", 1f)
                .build();
            mongoTemplate.indexOps("instructor_profiles").ensureIndex(profilesTextIndex);
        }

        System.out.println(">>> MONGODB SEO INDEX STRATEGY COMPLETED SUCCESSFULLY.");
    }
}
