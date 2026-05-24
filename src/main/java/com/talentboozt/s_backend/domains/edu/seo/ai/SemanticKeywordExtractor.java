package com.talentboozt.s_backend.domains.edu.seo.ai;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * AI-Ready Semantic Keyword Extractor.
 * Parses course contexts to map domain-specific synonyms and local exam terminology,
 * optimizing ingestion indexes for LLM retrievers.
 */
@Service
public class SemanticKeywordExtractor {

    /**
     * Inspects dynamic text variables to isolate core educational ontologies and target clusters.
     */
    public List<String> extractKeywords(String title, String description) {
        List<String> keywords = new ArrayList<>();
        String sourceText = ((title != null ? title : "") + " " + (description != null ? description : "")).toLowerCase();

        // 1. Analyze Subject Ontologies (Physics / Biology / Maths / Chemistry)
        if (sourceText.contains("physics") || sourceText.contains("භෞතික") || sourceText.contains("இயற்பியல்")) {
            keywords.addAll(List.of("mechanics", "rotational dynamics", "thermodynamics", "al physics", "nie syllabus"));
        }
        if (sourceText.contains("math") || sourceText.contains("ගණිතය") || sourceText.contains("கணிதம்")) {
            keywords.addAll(List.of("combined mathematics", "calculus", "vectors", "integration", "trigonometry"));
        }
        if (sourceText.contains("chemistry") || sourceText.contains("රසායන") || sourceText.contains("வேதியியல்")) {
            keywords.addAll(List.of("organic chemistry", "inorganic chemistry", "equilibrium", "chemical kinetics"));
        }

        // 2. Map Standard Localized Search Synonyms
        keywords.addAll(List.of("online revision classes", "sri lanka exam prep", "syllabus revisions", "past papers"));
        return keywords;
    }
}
