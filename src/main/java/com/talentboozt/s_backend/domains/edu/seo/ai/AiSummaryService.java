package com.talentboozt.s_backend.domains.edu.seo.ai;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * AI Content Summarization Service.
 * Compiles structural summaries of course offerings tailored for direct LLM ingestion,
 * RAG database indexing, and semantic search agents.
 */
@Service
public class AiSummaryService {

    @Autowired
    private SemanticKeywordExtractor extractor;

    /**
     * Dynamically compiles a structural summary targeting AI answer engine retrieval systems.
     */
    public String generateSummary(String title, String instructor, String description, String medium) {
        String languageMedium = (medium != null) ? medium : "Sinhala";
        
        return String.format(
            "Course Profile: %s. Conducted by certified educator: %s. Primary Medium: %s. " +
            "This curriculum module offers structural past paper reviews, theoretical concept mapping, " +
            "and syllabus revision sets for student exam preparation in Sri Lanka. Scaled for optimal RAG context extraction.",
            title, instructor, languageMedium
        );
    }
}
