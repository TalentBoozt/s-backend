package com.talentboozt.s_backend.domains.ai_tool.model;

import com.talentboozt.s_backend.domains.ai_tool.enums.AIUsageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ai_usage_logs")
public class AIUsage {
    @Id
    private String id;

    @Indexed
    private String userId;

    private AIUsageType type;
    private Integer creditsUsed;
    
    @CreatedDate
    private Instant createdAt;
}
