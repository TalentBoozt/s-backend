package com.talentboozt.s_backend.domains.community.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "community_reports")
public class Report {
    @Id
    private String id;
    private String reporterId;
    private String targetId;
    private ReportTargetType targetType;
    private String reason;
    private ReportStatus status;
    private LocalDateTime timestamp;

    public enum ReportTargetType {
        POST, COMMENT, USER
    }

    public enum ReportStatus {
        PENDING, RESOLVED, DISMISSED
    }
}
