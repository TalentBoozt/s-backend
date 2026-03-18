package com.talentboozt.s_backend.domains.edu.dto.ai;

import lombok.Data;

@Data
public class AIGenerationRequest {
    private String topic;
    private String audienceLevel; // BEGINNER, INTERMEDIATE, ADVANCED
    private String tone; // PROFESSIONAL, CASUAL, TECHNICAL
    private Integer limit; // module/section limits
}
