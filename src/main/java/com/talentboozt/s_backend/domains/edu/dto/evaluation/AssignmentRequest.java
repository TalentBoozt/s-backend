package com.talentboozt.s_backend.domains.edu.dto.evaluation;

import lombok.Data;
import java.time.Instant;

@Data
public class AssignmentRequest {
    private String title;
    private String description;
    private String instructions;
    private String[] attachmentUrls;
    private Double maxScore;
    private Double weightage;
    private Instant dueDate;
    private Boolean isPublished;
}
