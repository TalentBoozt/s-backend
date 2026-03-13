package com.talentboozt.s_backend.domains.lifeplanner.planner.dto;

import com.talentboozt.s_backend.domains.lifeplanner.planner.model.DailySchedule;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResponseDTO {
    private DailySchedule schedule;
    private int currentStreak;
    private Integer todayMood;
    private int missedTaskCount;
    private boolean hasDrift;
}
