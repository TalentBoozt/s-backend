package com.talentboozt.s_backend.domains.pipeline.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "pipelines")
public class PipelineModel {
    @Id
    private String id;
    private String name;
    private String organizationId;
    private String jobId; // Optional: specific to a job, or global for org
    
    private List<PipelineStage> stages;
    
    private Instant createdAt;
    private Instant updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PipelineStage {
        private String id;
        private String name; // APPLIED, SCREENING, TECHNICAL, CULTURAL, OFFER
        private int order;
        private boolean autoTransition;
        private List<String> requiredActions; // e.g. "SCORE_ABOVE_80", "SEND_EMAIL"
    }
}
