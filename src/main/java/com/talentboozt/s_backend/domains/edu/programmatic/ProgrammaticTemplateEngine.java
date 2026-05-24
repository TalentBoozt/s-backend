package com.talentboozt.s_backend.domains.edu.programmatic;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * Programmatic Page Content Template Engine.
 * Dynamically parses target slugs to assemble rich SEO metadata,
 * structural markdown-friendly body elements, and semantic ontology lists.
 */
@Service
public class ProgrammaticTemplateEngine {

    /**
     * Compiles dynamic landing page document definitions.
     */
    public ProgrammaticPageDocument buildPage(String slug) {
        ProgrammaticPageDocument doc = new ProgrammaticPageDocument();
        doc.setSlug(slug);
        
        String cleanSlug = slug.replace("tuition/", "").replace("-", " ");
        String[] parts = cleanSlug.split("/");
        
        String subject = parts.length > 0 ? capitalize(parts[0]) : "Advanced Level";
        String factor = parts.length > 1 ? capitalize(parts[1]) : "Sri Lanka";

        if (slug.contains("best-al-")) {
            String sub = capitalize(slug.replace("best-al-", "").replace("-teachers-sri-lanka", ""));
            doc.setTitle("Best G.C.E. A/L " + sub + " Teachers in Sri Lanka | Verified Directory");
            doc.setSeoTitle("Best A/L " + sub + " Teachers in Sri Lanka | Talnova");
            doc.setSeoDescription("Discover the top-rated " + sub + " teachers and revision classes in Sri Lanka. Connect with certified tutors online.");
        } else if (slug.contains("free-al-")) {
            String sub = capitalize(slug.replace("free-al-", "").replace("-revision-notes", ""));
            doc.setTitle("Free A/L " + sub + " Revision Notes & Syllabus Guides");
            doc.setSeoTitle("Download Free A/L " + sub + " Revision Notes | Talnova");
            doc.setSeoDescription("Access free A/L " + sub + " revision notes, past papers, and study guides aligned with the latest NIE syllabus.");
        } else {
            doc.setTitle("Expert G.C.E. A/L " + subject + " Tuition Classes in " + factor);
            doc.setSeoTitle("A/L " + subject + " Tuition Classes (" + factor + ") | Talnova");
            doc.setSeoDescription("Find the best Advanced Level " + subject + " classes in " + factor + ". Compare certified teacher directories, fees, and timetables.");
        }

        // Add programmatic crawl blocks
        List<String> blocks = new ArrayList<>();
        blocks.add("<h3>Premium Exam Guidance Platform</h3>");
        blocks.add("<p>Welcome to Talnova's comprehensive A/L revision portal. Here you can explore expert tuition structures, syllabus indexes, past paper breakdowns, and exam prep courses designed to optimize student performance.</p>");
        doc.setContentBlocks(blocks);

        // Add targeted keywords
        doc.setSemanticKeywords(List.of(subject.toLowerCase(), factor.toLowerCase(), "tuition center", "al exam", "past papers"));
        
        return doc;
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return "";
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
