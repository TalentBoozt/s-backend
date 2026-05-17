package com.talentboozt.s_backend.domains.ai_orchestration.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ai_tasks")
public class AiTask {
    @Id
    private String id;
    private String type; // RESUME_PARSING, ATS_ANALYSIS, JOB_DESCRIPTION_GEN, CANDIDATE_RANKING, INSIGHT_GENERATION
    private String status; // PENDING, PROCESSING, COMPLETED, FAILED
    
    private String provider; // GEMINI, OPENAI, ANTHROPIC
    private Map<String, Object> inputData;
    private Map<String, Object> resultData;
    
    private String errorMessage;
    private int retryCount;
    
    private Instant createdAt;
    private Instant updatedAt;
    private Instant completedAt;
}
