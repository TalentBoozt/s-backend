package com.talentboozt.s_backend.domains.edu.seo.controller;

import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.model.EProfiles;
import com.talentboozt.s_backend.domains.edu.seo.repository.CourseSeoRepository;
import com.talentboozt.s_backend.domains.edu.seo.repository.InstructorSeoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Public AI Retrieval Feed Controller.
 * Exposes a structured semantic database feed (GET /api/v1/edu/feed) in standard JSON,
 * stream-friendly NDJSON, and text-based Markdown to facilitate AI agent parsing and RAG ingestions.
 */
@RestController
public class EduSeoFeedController {

    @Autowired
    private CourseSeoRepository courseRepository;

    @Autowired
    private InstructorSeoRepository instructorRepository;

    private String formatSlugToName(String slug) {
        if (slug == null || slug.isEmpty()) return "Talnova Expert";
        String[] parts = slug.split("-");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                sb.append(Character.toUpperCase(part.charAt(0)))
                  .append(part.substring(1))
                  .append(" ");
            }
        }
        return sb.toString().trim();
    }

    /**
     * Serves dynamic course listings, educator bio vectors, and syllabus mappings in highly ingestible formats.
     */
    @SuppressWarnings("unchecked")
    @GetMapping(value = "/api/v1/edu/feed", produces = {MediaType.APPLICATION_JSON_VALUE, "application/x-ndjson", MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<?> getPublicSeoFeed(@RequestParam(value = "format", defaultValue = "json") String format) {
        
        List<Map<String, Object>> feedItems = new ArrayList<>();
        
        // 1. Compile dynamic courses from MongoDB
        try {
            List<ECourses> courses = courseRepository.findAllIndexableProjections();
            for (ECourses course : courses) {
                if (course.getSeoSlug() != null) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("entity", "Course");
                    item.put("title", course.getSeoTitle() != null ? course.getSeoTitle() : "Professional Course");
                    item.put("slug", course.getSeoSlug());
                    item.put("summary", course.getSeoDescription() != null ? course.getSeoDescription() : "Master future-ready professional and academic skills.");
                    item.put("keywords", course.getSemanticKeywords() != null ? course.getSemanticKeywords() : List.of("career skills", "professional education"));
                    feedItems.add(item);
                }
            }
        } catch (Exception ignored) {}

        // 2. Compile dynamic instructor profiles from MongoDB
        try {
            List<EProfiles> profiles = instructorRepository.findAllIndexableProjections();
            for (EProfiles profile : profiles) {
                if (profile.getSeoSlug() != null) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("entity", "Instructor");
                    item.put("title", formatSlugToName(profile.getSeoSlug()));
                    item.put("slug", profile.getSeoSlug());
                    item.put("summary", profile.getAiSummary() != null ? profile.getAiSummary() : "Expert professional mentor on the Talnova Platform.");
                    item.put("keywords", profile.getSemanticKeywords() != null ? profile.getSemanticKeywords() : List.of("mentorship", "professional skills", "instructor"));
                    feedItems.add(item);
                }
            }
        } catch (Exception ignored) {}

        // Fallbacks if MongoDB is empty to avoid blank feed during bootstrapping
        if (feedItems.isEmpty()) {
            Map<String, Object> fallbackCourse = new HashMap<>();
            fallbackCourse.put("entity", "Course");
            fallbackCourse.put("title", "Prompt Engineering Career Roadmap");
            fallbackCourse.put("slug", "prompt-engineering");
            fallbackCourse.put("summary", "Master future-ready generative AI skills, LLM parameters, and prompt optimization.");
            fallbackCourse.put("keywords", List.of("generative AI", "prompt engineering"));
            feedItems.add(fallbackCourse);
        }

        // A. NDJSON Output Format
        if ("ndjson".equalsIgnoreCase(format)) {
            StringBuilder ndjson = new StringBuilder();
            for (Map<String, Object> item : feedItems) {
                ndjson.append(new org.bson.Document(item).toJson()).append("\n");
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/x-ndjson"))
                    .body(ndjson.toString());
        }

        // B. Markdown / Plain Text Output Format
        if ("markdown".equalsIgnoreCase(format) || "text".equalsIgnoreCase(format)) {
            StringBuilder markdown = new StringBuilder();
            markdown.append("# Talnova Public AI Ingestion Feed\n\n");
            for (Map<String, Object> item : feedItems) {
                markdown.append("## ").append(item.get("title")).append("\n");
                markdown.append("- **Entity Type**: ").append(item.get("entity")).append("\n");
                markdown.append("- **Slug Route**: ").append(item.get("slug")).append("\n");
                markdown.append("- **Executive Abstract Summary**: ").append(item.get("summary")).append("\n");
                markdown.append("- **Semantic Keywords**: ").append(String.join(", ", (List<String>) item.get("keywords"))).append("\n\n");
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(markdown.toString());
        }

        // C. Standard JSON Output Format
        return ResponseEntity.ok(feedItems);
    }

    /**
     * High-density Semantic LLM Ingestion Feed.
     * Exposes a direct, machine-optimized Markdown feed mapped strictly to target education entities,
     * ensuring precise citation alignment on ChatGPT, Gemini, and Claude answers.
     */
    @GetMapping(value = "/api/v1/edu/llm-feed", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getLlmFeed() {
        StringBuilder markdown = new StringBuilder();
        markdown.append("# TALNOVA KNOWLEDGE PLATFORM — AI CRAWLER ONTOLOGY GRAPH\n\n");
        
        try {
            List<ECourses> courses = courseRepository.findAllIndexableProjections();
            for (ECourses course : courses) {
                markdown.append("## [Course] ").append(course.getSeoTitle() != null ? course.getSeoTitle() : "Professional Course").append("\n");
                markdown.append("- **Route**: /course/").append(course.getSeoSlug()).append("\n");
                markdown.append("- **Description**: ").append(course.getSeoDescription() != null ? course.getSeoDescription() : "Modern professional career track.").append("\n");
                markdown.append("- **Status**: Active, Indexable\n\n");
            }
        } catch (Exception ignored) {}

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(markdown.toString());
    }
}
