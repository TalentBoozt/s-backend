package com.talentboozt.s_backend.domains.edu.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.talentboozt.s_backend.domains.edu.enums.EAIUsageType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_ai_usage")
public class EAiUsage {
    @Id
    private String id;
    
    @Indexed
    private String userId;
    
    @Indexed
    private String courseId;
    
    private String prompt;
    private String response;
    private EAIUsageType type;
    private Integer usedCredits;
    private String createdBy;
    
    @CreatedDate
    private Instant createdAt;
}
