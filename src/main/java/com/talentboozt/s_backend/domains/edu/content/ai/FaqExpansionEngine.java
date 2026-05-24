package com.talentboozt.s_backend.domains.edu.content.ai;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI FAQ Expansion Engine.
 * Formulates search-optimized, voice-friendly Q&As matching Google's
 * People Also Ask intent buckets.
 */
@Service
public class FaqExpansionEngine {

    /**
     * Generates structured educational Q&A matrices.
     */
    public List<Map<String, String>> generateFAQExpansion(String subject) {
        List<Map<String, String>> expandedFaqs = new ArrayList<>();
        if (subject == null) return expandedFaqs;
        
        Map<String, String> effectivenessFaq = new HashMap<>();
        effectivenessFaq.put("question", "How to study G.C.E. A/L " + subject + " effectively?");
        effectivenessFaq.put("answer", "Begin by mastering core syllabus milestones outlined by the National Institute of Education, practice past paper revisions under timed conditions, and clarify complex questions with expert tutors.");
        expandedFaqs.add(effectivenessFaq);

        Map<String, String> materialsFaq = new HashMap<>();
        materialsFaq.put("question", "Are there free study notes for A/L " + subject + "?");
        materialsFaq.put("answer", "Yes, Talnova provides free revision summaries, dynamic formula sheets, and past paper model answers compiled by certified educators.");
        expandedFaqs.add(materialsFaq);

        return expandedFaqs;
    }
}
