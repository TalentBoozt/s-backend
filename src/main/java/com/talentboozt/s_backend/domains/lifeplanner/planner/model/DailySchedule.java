package com.talentboozt.s_backend.domains.lifeplanner.planner.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.util.List;

@Data
@Document(collection = "lp_daily_schedules")
@CompoundIndex(name = "user_date_idx", def = "{'userId': 1, 'scheduleDate': 1}", unique = true)
public class DailySchedule {
    @Id
    private String scheduleId;
    @Indexed
    private String planId;
    @Indexed
    private String userId;
    private LocalDate scheduleDate;
    private List<ScheduleTask> tasks;
    @JsonProperty("isCompleted")
    private boolean isCompleted;

    @Data
    public static class ScheduleTask {
        private String taskId;
        private String title;
        private String estimatedTime;
        private String category;
        private String startTime;
        private String endTime;
        @JsonProperty("isCompleted")
        private boolean isCompleted;
        private String completedAt;
    }
}
