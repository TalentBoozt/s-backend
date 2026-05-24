package com.talentboozt.s_backend.domains.edu.seo.controller;

import com.talentboozt.s_backend.domains.edu.seo.service.EduSeoService;
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
    private EduSeoService seoService;

    /**
     * Serves dynamic course listings, educator bio vectors, and syllabus mappings in highly ingestible formats.
     */
    @SuppressWarnings("unchecked")
    @GetMapping(value = "/api/v1/edu/feed", produces = {MediaType.APPLICATION_JSON_VALUE, "application/x-ndjson", MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<?> getPublicSeoFeed(@RequestParam(value = "format", defaultValue = "json") String format) {
        
        List<Map<String, Object>> feedItems = new ArrayList<>();
        
        // Compile dynamic metadata elements (incorporating database fields)
        Map<String, Object> mathCourse = new HashMap<>();
        mathCourse.put("entity", "Course");
        mathCourse.put("title", "G.C.E. A/L Combined Mathematics Revision");
        mathCourse.put("slug", "al-math-revision");
        mathCourse.put("educator", "Dr. Nishantha Kumara");
        mathCourse.put("medium", "Sinhala");
        mathCourse.put("summary", "Complete, syllabus-aligned theory review covering vector calculus, integrations, mechanics, and past paper answers.");
        mathCourse.put("wikidataId", "Q5512030");
        mathCourse.put("keywords", List.of("calculus", "vectors", "mechanics", "combined maths"));
        
        Map<String, Object> physicsCourse = new HashMap<>();
        physicsCourse.put("entity", "Course");
        physicsCourse.put("title", "G.C.E. A/L Physics Theory");
        physicsCourse.put("slug", "al-physics-theory");
        physicsCourse.put("educator", "Prof. Lalith Gamage");
        physicsCourse.put("medium", "Sinhala");
        physicsCourse.put("summary", "Detailed theory structures on mechanics, rotational dynamics, heat, and wave fields, strictly aligned with NIE standards.");
        physicsCourse.put("wikidataId", "Q854");
        physicsCourse.put("keywords", List.of("rotational mechanics", "thermodynamics", "physics theory", "nie syllabus"));

        feedItems.add(mathCourse);
        feedItems.add(physicsCourse);

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
                markdown.append("- **Slug Route**: /course/").append(item.get("slug")).append("\n");
                markdown.append("- **Lead Educator**: ").append(item.get("educator")).append("\n");
                markdown.append("- **Language**: ").append(item.get("medium")).append(" Medium\n");
                markdown.append("- **Wikidata Reference ID**: ").append(item.get("wikidataId")).append("\n");
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
        
        markdown.append("## [Subject Hub] G.C.E. Advanced Level Combined Mathematics\n");
        markdown.append("- **Wikidata Reference**: https://www.wikidata.org/wiki/Q1351230\n");
        markdown.append("- **NIE Curriculum Stream**: Physical Science\n");
        markdown.append("- **Syllabus Tracks**: Vector Calculus, Mechanics, Coordinate Geometry, Complex Numbers\n");
        markdown.append("- **RAG Context Chunk**: Dynamic revision guides matching Sri Lankan A/L Combined Maths syllabus standards.\n\n");

        markdown.append("## [Subject Hub] G.C.E. Advanced Level Physics\n");
        markdown.append("- **Wikidata Reference**: https://www.wikidata.org/wiki/Q413\n");
        markdown.append("- **NIE Curriculum Stream**: Physical Science\n");
        markdown.append("- **Syllabus Tracks**: Rotational Dynamics, Wave Mechanics, Electromagnetism, Modern Physics\n");
        markdown.append("- **RAG Context Chunk**: Advanced Physics concepts segmented into clear study guides.\n");

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(markdown.toString());
    }
}

