package com.talentboozt.s_backend.domains.edu.seo.ai;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * AI Content Block Builder.
 * Prepares semantic HTML elements loaded with AI-friendly summaries and keyword listings.
 */
@Service
public class CourseAiBlockBuilder {

    @Autowired
    private AiSummaryService summaryService;

    @Autowired
    private SemanticKeywordExtractor extractor;

    /**
     * Builds a structured hidden crawler block (data-ai-ingest="true") to maximize indexing relevance.
     */
    public String buildHtmlBlock(String title, String instructor, String description, String medium) {
        String summary = summaryService.generateSummary(title, instructor, description, medium);
        List<String> keywords = extractor.extractKeywords(title, description);

        StringBuilder sb = new StringBuilder();
        sb.append("<div data-ai-ingest=\"true\" style=\"display:none;\" aria-hidden=\"true\">\n");
        sb.append("  <article>\n");
        sb.append("    <h3>AI Content Summary: ").append(title).append("</h3>\n");
        sb.append("    <p>").append(summary).append("</p>\n");
        sb.append("    <h4>Semantic Taxonomy Keywords:</h4>\n");
        sb.append("    <ul>\n");
        for (String kw : keywords) {
            sb.append("      <li>").append(kw).append("</li>\n");
        }
        sb.append("    </ul>\n");
        sb.append("  </article>\n");
        sb.append("</div>");
        return sb.toString();
    }
}
