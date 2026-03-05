package com.talentboozt.s_backend.domains.resume.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * AI generation response structure.
 * Polymorphic — frontends inspect "type" to know how to apply it.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiResumeResponse {
    /** Mirrors req.type: summary | experience | skills | education */
    private String type;
    /** For type=summary / education.description */
    private String generatedText;
    /** For type=experience — list of improved bullet points */
    private List<String> bulletPoints;
    /** For type=skills — list of suggested skill names */
    private List<String> suggestedSkills;
    /** How many AI calls remain for this resume */
    private int aiUsageRemaining;
    /** Human-readable note from AI (optional) */
    private String note;
}
