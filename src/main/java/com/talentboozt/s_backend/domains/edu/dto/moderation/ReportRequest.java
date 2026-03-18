package com.talentboozt.s_backend.domains.edu.dto.moderation;

import lombok.Data;
import com.talentboozt.s_backend.domains.edu.enums.EReportReason;

@Data
public class ReportRequest {
    private String reporterId;
    private String targetEntityId;
    private String entityType; // COURSE, USER, REVIEW
    private EReportReason reason;
    private String description;
}
