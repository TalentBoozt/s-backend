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
        if (mongoTemplate.collectionExists("edu_courses")) {
            // Drop existing text index if it exists to avoid weight conflicts
            try {
                for (org.bson.Document index : mongoTemplate.getCollection("edu_courses").listIndexes()) {
                    org.bson.Document key = (org.bson.Document) index.get("key");
                    if (key != null && key.containsKey("_fts")) {
                        String indexName = index.getString("name");
                        System.out.println("Dropping conflicting course text index: " + indexName);
                        mongoTemplate.indexOps("edu_courses").dropIndex(indexName);
                    }
                }
            } catch (Exception e) {
                System.out.println("Failed to drop existing course text index: " + e.getMessage());
            }

            // Unique Sparse Slug Index for bot mapping
            try {
                mongoTemplate.indexOps("edu_courses").ensureIndex(
                    new Index().on("seoSlug", Sort.Direction.ASC).named("seoSlug").unique().sparse()
                );
            } catch (Exception e) {
                System.out.println("Encountered warning ensuring seoSlug index on edu_courses: " + e.getMessage());
            }

            // Sparse Lang Group Index for alternate mapping
            try {
                mongoTemplate.indexOps("edu_courses").ensureIndex(
                    new Index().on("localizedLangGroupId", Sort.Direction.ASC).named("localizedLangGroupId").sparse()
                );
            } catch (Exception e) {
                System.out.println("Encountered warning ensuring localizedLangGroupId index on edu_courses: " + e.getMessage());
            }

            // Published/Updated Sorting Indices
            try {
                mongoTemplate.indexOps("edu_courses").ensureIndex(
                    new Index().on("publishedAt", Sort.Direction.DESC).named("publishedAtDesc")
                );
            } catch (Exception e) {
                System.out.println("Encountered warning ensuring publishedAtDesc index on edu_courses: " + e.getMessage());
            }
            try {
                mongoTemplate.indexOps("edu_courses").ensureIndex(
                    new Index().on("updatedAt", Sort.Direction.DESC).named("updatedAtDesc")
                );
            } catch (Exception e) {
                System.out.println("Encountered warning ensuring updatedAtDesc index on edu_courses: " + e.getMessage());
            }

            // Composite Text Index for high-density keyword searches
            try {
                TextIndexDefinition coursesTextIndex = new TextIndexDefinition.TextIndexDefinitionBuilder()
                    .named("ECourses_TextIndex")
                    .onField("seoTitle", 3f)
                    .onField("seoDescription", 2f)
                    .onField("semanticKeywords", 1f)
                    .onField("title", 1f)
                    .onField("description", 1f)
                    .onField("keywords", 1f)
                    .build();
                mongoTemplate.indexOps("edu_courses").ensureIndex(coursesTextIndex);
            } catch (Exception e) {
                System.out.println("Encountered warning ensuring ECourses_TextIndex: " + e.getMessage());
            }
        }

        // 2. Initialize Instructor Profiles Indices
        if (mongoTemplate.collectionExists("edu_profiles")) {
            // Drop existing text index if it exists to avoid weight conflicts
            try {
                for (org.bson.Document index : mongoTemplate.getCollection("edu_profiles").listIndexes()) {
                    org.bson.Document key = (org.bson.Document) index.get("key");
                    if (key != null && key.containsKey("_fts")) {
                        String indexName = index.getString("name");
                        System.out.println("Dropping conflicting profile text index: " + indexName);
                        mongoTemplate.indexOps("edu_profiles").dropIndex(indexName);
                    }
                }
            } catch (Exception e) {
                System.out.println("Failed to drop existing profile text index: " + e.getMessage());
            }

            // Unique Sparse Slug Index for bot mapping
            try {
                mongoTemplate.indexOps("edu_profiles").ensureIndex(
                    new Index().on("seoSlug", Sort.Direction.ASC).named("seoSlug").unique().sparse()
                );
            } catch (Exception e) {
                System.out.println("Encountered warning ensuring seoSlug index on edu_profiles: " + e.getMessage());
            }

            // Composite Text Index for educator lookup
            try {
                TextIndexDefinition profilesTextIndex = new TextIndexDefinition.TextIndexDefinitionBuilder()
                    .named("EProfiles_TextIndex")
                    .onField("semanticKeywords", 1f)
                    .build();
                mongoTemplate.indexOps("edu_profiles").ensureIndex(profilesTextIndex);
            } catch (Exception e) {
                System.out.println("Encountered warning ensuring EProfiles_TextIndex: " + e.getMessage());
            }
        }

        System.out.println(">>> MONGODB SEO INDEX STRATEGY COMPLETED SUCCESSFULLY.");
    }
}
