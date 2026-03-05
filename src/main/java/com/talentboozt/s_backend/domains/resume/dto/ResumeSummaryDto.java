package com.talentboozt.s_backend.domains.resume.dto;

import com.talentboozt.s_backend.domains.resume.model.ResumeModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Lightweight summary returned in the Dashboard / resume list.
 * Does NOT include heavy nested sections to reduce payload.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResumeSummaryDto {
    private String id;
    private String title;
    private String templateId;
    private int completionScore;
    private int atsScore;
    private int aiUsageCount;
    private int aiUsageRemaining;
    private Instant updatedAt;
    private Instant createdAt;

    public static final int MAX_AI_USAGE = 5;

    public static ResumeSummaryDto from(ResumeModel m) {
        ResumeSummaryDto dto = new ResumeSummaryDto();
        dto.id = m.getId();
        dto.title = m.getTitle();
        dto.templateId = m.getTemplateId();
        dto.completionScore = m.getCompletionScore();
        dto.atsScore = m.getAtsScore();
        dto.aiUsageCount = m.getAiUsageCount();
        dto.aiUsageRemaining = Math.max(0, MAX_AI_USAGE - m.getAiUsageCount());
        dto.updatedAt = m.getUpdatedAt();
        dto.createdAt = m.getCreatedAt();
        return dto;
    }
}
