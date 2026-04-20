package com.talentboozt.s_backend.domains.edu.dto.analytics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LearnerAnalyticsDTO {
    private String learnerId;
    private Integer totalCoursesEnrolled;
    private Integer completedCourses;
    private Integer currentStreak;
    private Integer longestStreak;
    private Integer totalCertificates;
    private Integer minutesToday;
    private Integer nextMilestoneProgress;
    private Double avgCompletion;
    private String learnerLevel;
}
