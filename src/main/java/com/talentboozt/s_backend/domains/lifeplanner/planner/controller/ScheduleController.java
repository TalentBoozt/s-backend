package com.talentboozt.s_backend.domains.lifeplanner.planner.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.talentboozt.s_backend.domains.lifeplanner.planner.model.DailySchedule;
import com.talentboozt.s_backend.domains.lifeplanner.planner.service.DailyScheduleService;
import com.talentboozt.s_backend.domains.lifeplanner.planner.service.TaskCompletionService;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lifeplanner/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final DailyScheduleService dailyScheduleService;
    private final TaskCompletionService taskCompletionService;

    @GetMapping("/today")
    public ResponseEntity<DailySchedule> getTodaySchedule(@RequestHeader("x-user-id") String userId) {
        DailySchedule schedule = dailyScheduleService.getTodaySchedule(userId);
        if (schedule == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(schedule);
    }

    @PostMapping("/complete-task")
    public ResponseEntity<Void> completeTask(@RequestBody Map<String, String> payload, @RequestHeader("x-user-id") String userId) {
        String scheduleId = payload.get("scheduleId");
        String taskId = payload.get("taskId");
        taskCompletionService.markTaskAsCompleted(userId, scheduleId, taskId);
        return ResponseEntity.ok().build();
    }
}
