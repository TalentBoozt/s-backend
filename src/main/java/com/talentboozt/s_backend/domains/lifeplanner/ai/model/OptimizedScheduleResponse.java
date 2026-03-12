package com.talentboozt.s_backend.domains.lifeplanner.ai.model;

import lombok.Data;
import java.util.List;

@Data
public class OptimizedScheduleResponse {
    private List<RescheduledTask> rescheduledTasks;
    private String rationale;

    @Data
    public static class RescheduledTask {
        private String title;
        private String estimatedTime;
        private String assignedDay;
        private String category;
    }
}
