package com.talentboozt.s_backend.domains.lifeplanner.ai.model;

import lombok.Data;
import java.util.List;

@Data
public class PlanResponse {
    private List<RoadmapItem> roadmap;
    private List<WeeklyPlan> weeklyPlans;
    private List<DailyTask> dailyTasks;

    @Data
    public static class RoadmapItem {
        private String title;
        private String description;
        private String expectedDuration;
    }

    @Data
    public static class WeeklyPlan {
        private int weekNumber;
        private String focusArea;
        private List<String> objectives;
    }

    @Data
    public static class DailyTask {
        private String title;
        private String estimatedTime;
        private String category;
    }
}
