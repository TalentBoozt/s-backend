package com.talentboozt.s_backend.domains.lifeplanner.analytics.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class AnalyticsDTO {
    private int totalTasksCompleted;
    private int totalTasksPending;
    private double completionRate;
    private int currentStreak;
    private int bestStreak;
    private double averageMood;
    private List<DailyStats> dailyStats;       // last 14 days
    private Map<String, Integer> categoryBreakdown;
    private Map<String, Integer> priorityBreakdown;
    private double studyHoursThisWeek;

    @Data
    @Builder
    public static class DailyStats {
        private String date;
        private int tasksCompleted;
        private int totalTasks;
        private Integer moodScore;
    }
}
