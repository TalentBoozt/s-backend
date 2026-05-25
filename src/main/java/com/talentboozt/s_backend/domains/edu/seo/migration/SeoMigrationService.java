package com.talentboozt.s_backend.domains.edu.seo.migration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.bson.Document;
import java.util.List;
import java.util.UUID;

/**
 * MongoDB SEO Schema Migration Service.
 * Detects dynamic missing fields on start, upgrades legacy documents, and backfills
 * seoSlugs, AI parameters, canonical paths, and schema properties in a batch-resumable mode.
 */
@Service
public class SeoMigrationService {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Executes dynamic document upgrades on courses and profiles collections safely.
     */
    public void runSchemaMigration() {
        System.out.println(">>> STARTING MONGODB SEO SCHEMA EVOLUTION RUNNER...");

        // 1. Upgrading Course Documents
        if (mongoTemplate.collectionExists("edu_courses")) {
            Query courseQuery = new Query(Criteria.where("seoSlug").exists(false));
            List<Document> courses = mongoTemplate.find(courseQuery, Document.class, "edu_courses");
            System.out.println("Found " + courses.size() + " legacy course documents requiring SEO upgrades.");

            for (Document course : courses) {
                String title = course.getString("title");
                if (title == null) title = course.getString("name");
                if (title == null) title = "course-" + UUID.randomUUID().toString().substring(0, 8);

                String rawSlug = title.toLowerCase()
                        .replaceAll("[^a-z0-9\\s]", "")
                        .replaceAll("\\s+", "-");
                String finalSlug = rawSlug + "-" + UUID.randomUUID().toString().substring(0, 4);

                Query updateQuery = new Query(Criteria.where("_id").is(course.get("_id")));
                Update update = new Update()
                        .set("seoSlug", finalSlug)
                        .set("seoTitle", title + " | Talnova EDU")
                        .set("seoDescription", "Enroll in our specialized exam-aligned revision classes. Master subject tracks with certified instructors on Talnova.")
                        .set("seoKeywords", "talnova, class, online, sri lanka")
                        .set("indexable", true)
                        .set("aiReady", true)
                        .set("aiSummary", "Self-paced study module for exam syllabus segments.")
                        .set("semanticKeywords", List.of("curriculum", "lessons", "tutorials"))
                        .set("localizedLangGroupId", "group-" + UUID.randomUUID().toString().substring(0, 8))
                        .set("canonicalUrl", "https://edu.talnova.io/course/" + finalSlug)
                        .set("publishedAt", new java.util.Date())
                        .set("updatedAt", new java.util.Date());

                mongoTemplate.updateFirst(updateQuery, update, "edu_courses");
            }
        }

        // 2. Upgrading Instructor Profile Documents
        if (mongoTemplate.collectionExists("edu_profiles")) {
            Query profileQuery = new Query(Criteria.where("seoSlug").exists(false));
            List<Document> profiles = mongoTemplate.find(profileQuery, Document.class, "edu_profiles");
            System.out.println("Found " + profiles.size() + " legacy instructor documents requiring SEO upgrades.");

            for (Document profile : profiles) {
                String name = profile.getString("name");
                if (name == null) name = "instructor-" + UUID.randomUUID().toString().substring(0, 8);

                String rawSlug = name.toLowerCase()
                        .replaceAll("[^a-z0-9\\s]", "")
                        .replaceAll("\\s+", "-");
                String finalSlug = rawSlug + "-" + UUID.randomUUID().toString().substring(0, 4);

                Query updateQuery = new Query(Criteria.where("_id").is(profile.get("_id")));
                Update update = new Update()
                        .set("seoSlug", finalSlug)
                        .set("canonicalUrl", "https://edu.talnova.io/p/" + finalSlug)
                        .set("indexable", true)
                        .set("aiSummary", "Verified educator on the Talnova platform.")
                        .set("semanticKeywords", List.of("lectures", "tutoring", "academics"))
                        .set("schemaJsonLd", "{}");

                mongoTemplate.updateFirst(updateQuery, update, "edu_profiles");
            }
        }

        System.out.println(">>> MONGODB SEO SCHEMA EVOLUTION RUNNER COMPLETED SUCCESSFULLY.");
        
        // 3. Seeding Topical Authority Pages (Pillar Pages)
        if (mongoTemplate.collectionExists("edu_programmatic_pages")) {
            seedPillarPage("learn-ai", "Learn Artificial Intelligence & Automation | Talnova Academy",
                "Master AI Prompt Engineering, automated workflows, and modern machine learning tools to accelerate your remote career development.",
                List.of("AI Automation", "Prompt Engineering", "Future Skills"));
            seedPillarPage("learn-freelancing", "How to Start Freelancing & Remote Work in 2026 | Talnova Academy",
                "Discover the complete blueprint to launch a global remote freelancing career. Build portfolios, negotiate with clients, and work from anywhere.",
                List.of("Freelancing Blueprint", "Remote Gigs", "Client Acquisition"));
            seedPillarPage("learn-web-development", "Professional Software Development & Coding Bootcamp | Talnova Academy",
                "Learn frontend engineering, backend development, and scalable cloud tools with industry-grade certified training tracks.",
                List.of("Frontend Engineering", "Full Stack Development", "React and Spring Boot"));
        }
    }

    private void seedPillarPage(String slug, String title, String description, List<String> keywords) {
        Query q = new Query(Criteria.where("slug").is(slug));
        if (!mongoTemplate.exists(q, "edu_programmatic_pages")) {
            Document doc = new Document()
                .append("slug", slug)
                .append("title", title.split(" \\| ")[0])
                .append("seoTitle", title)
                .append("seoDescription", description)
                .append("contentBlocks", List.of(
                    "Overview: Acquire highly specialized modern skills directly mapped to premium remote work careers.",
                    "Syllabus Tracks: Standardized modules covering technical workflows, software tools, and portfolio building.",
                    "Outcome Strategy: Gain certified credentials, launch freelance portfolios, and secure verified remote employer contracts."
                ))
                .append("semanticKeywords", keywords)
                .append("indexable", true)
                .append("lang", "en-LK")
                .append("generatedAt", new java.util.Date())
                .append("freshnessScore", 1.0);
            mongoTemplate.insert(doc, "edu_programmatic_pages");
            System.out.println("Seeded topical authority pillar page: " + slug);
        }
    }
}
