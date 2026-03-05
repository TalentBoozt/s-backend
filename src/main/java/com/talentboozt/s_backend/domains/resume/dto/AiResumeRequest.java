package com.talentboozt.s_backend.domains.resume.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI generation request body from the frontend.
 * type: summary | experience | skills | education
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiResumeRequest {
    /** The resume ID — used to enforce per-resume AI usage limits */
    private String resumeId;
    /** Type of content to generate */
    private String type;
    /** Free-form context (existing text, job title, skills list, etc.) */
    private String context;
    /** Optional: target job description to tailor content */
    private String jobDescription;
}
