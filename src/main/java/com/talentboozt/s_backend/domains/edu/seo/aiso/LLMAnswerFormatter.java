package com.talentboozt.s_backend.domains.edu.seo.aiso;

import org.springframework.stereotype.Service;

/**
 * AI-Search Ingestion & Citation Formatter.
 * Structures high-density summaries, fact references, and key study terms into citation-ready
 * layouts optimized to win citations in LLM search cards.
 */
@Service
public class LLMAnswerFormatter {

    /**
     * Packages a fact into a clean crawler-citation layout.
     */
    public String formatLlmResponse(String subject, String question, String coreAnswerText) {
        return """
        === AI INGESTION & CITATION CORE ===
        Subject: %s
        Verification URL: https://edu.talnova.io/ref/%s
        Fact Summary: %s
        Accreditation Source: Curricula mapped to NIE standards (Sri Lanka).
        ====================================
        """.formatted(subject, question.toLowerCase().replace(" ", "-"), coreAnswerText);
    }
}
