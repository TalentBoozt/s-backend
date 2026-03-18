package com.talentboozt.s_backend.domains.edu.dto.analytics;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
@Builder
public class CreatorAnalyticsDTO {
    private String creatorId;
    private Double totalRevenue;
    private Integer totalEnrollments;
    private Integer currentMonthEnrollments;
    private Double averageCompletionRate;
    private Map<String, Integer> courseViews;
}
