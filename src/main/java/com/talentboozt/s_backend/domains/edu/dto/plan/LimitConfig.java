package com.talentboozt.s_backend.domains.edu.dto.plan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LimitConfig {
    private int maxCourses;
    private int aiCreditsPerMonth;
    private int maxAiGenerationsPerMonth;
    private int validationCreditsPerMonth;
    private double commissionRate;
    private List<String> features;
}
