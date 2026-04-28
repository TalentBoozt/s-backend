package com.talentboozt.s_backend.domains.ai_tool.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ai_quotas")
public class AIQuota {
    @Id
    private String id;

    @Indexed(unique = true)
    private String userId;

    private Integer monthlyLimit;
    private Integer used;
    private Instant resetDate;
}
