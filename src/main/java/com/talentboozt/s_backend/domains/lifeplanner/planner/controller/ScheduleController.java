package com.talentboozt.s_backend.domains.lifeplanner.planner.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.talentboozt.s_backend.domains.lifeplanner.planner.model.DailySchedule;
import com.talentboozt.s_backend.domains.lifeplanner.planner.service.DailyScheduleService;
import com.talentboozt.s_backend.domains.lifeplanner.planner.service.TaskCompletionService;
import com.talentboozt.s_backend.domains.lifeplanner.planner.service.ScheduleRepairService;
import com.talentboozt.s_backend.domains.lifeplanner.ai.model.OptimizedScheduleResponse;
import com.talentboozt.s_backend.domains.lifeplanner.planner.service.StreakService;
import com.talentboozt.s_backend.domains.lifeplanner.planner.dto.ScheduleResponseDTO;
import com.talentboozt.s_backend.domains.lifeplanner.journal.service.MoodTrackingService;
import com.talentboozt.s_backend.domains.lifeplanner.journal.model.MoodEntry;
import java.util.Map;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lifeplanner/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final DailyScheduleService dailyScheduleService;
    private final TaskCompletionService taskCompletionService;
    private final ScheduleRepairService scheduleRepairService;
    private final StreakService streakService;
    private final MoodTrackingService moodTrackingService;

    @GetMapping("/today")
    public ResponseEntity<ScheduleResponseDTO> getTodaySchedule(@RequestHeader("x-user-id") String userId) {
        DailySchedule schedule = dailyScheduleService.getTodaySchedule(userId);
        int streak = streakService.calculateCurrentStreak(userId);
        Integer moodScore = moodTrackingService.getTodayMood(userId)
            .map(MoodEntry::getScore)
            .orElse(null);
        
        int missedCount = 0;
        if (schedule != null && schedule.getPlanId() != null) {
            missedCount = scheduleRepairService.getMissedTaskCountByPlan(schedule.getPlanId());
        }
        
        return ResponseEntity.ok(new ScheduleResponseDTO(schedule, streak, moodScore, missedCount, missedCount > 0));
    }

    @GetMapping("/plan/{planId}")
    public ResponseEntity<List<DailySchedule>> getSchedulesByPlanId(@PathVariable String planId) {
        return ResponseEntity.ok(dailyScheduleService.getSchedulesForPlan(planId));
    }

    @PostMapping("/complete-task")
    public ResponseEntity<Void> completeTask(@RequestBody Map<String, String> payload, @RequestHeader("x-user-id") String userId) {
        String scheduleId = payload.get("scheduleId");
        String taskId = payload.get("taskId");
        taskCompletionService.markTaskAsCompleted(userId, scheduleId, taskId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/repair")
    public ResponseEntity<OptimizedScheduleResponse> repairSchedule(@RequestBody Map<String, String> payload, @RequestHeader("x-user-id") String userId) {
        String planId = payload.get("planId");
        return ResponseEntity.ok(scheduleRepairService.repairSchedule(planId, userId));
    }

    @PostMapping("/add-task")
    public ResponseEntity<Void> addTask(@RequestBody Map<String, String> payload, @RequestHeader("x-user-id") String userId) {
        String title = payload.get("title");
        String category = payload.get("category");
        String estimatedTime = payload.get("estimatedTime");
        String priority = payload.get("priority");
        String notes = payload.get("notes");
        dailyScheduleService.addTaskToToday(userId, title, category, estimatedTime, priority, notes);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update-task")
    public ResponseEntity<Void> updateTask(@RequestBody Map<String, String> payload, @RequestHeader("x-user-id") String userId) {
        String scheduleId = payload.get("scheduleId");
        String taskId = payload.get("taskId");
        String title = payload.get("title");
        String category = payload.get("category");
        String estimatedTime = payload.get("estimatedTime");
        String priority = payload.get("priority");
        String notes = payload.get("notes");
        dailyScheduleService.updateTask(userId, scheduleId, taskId, title, category, estimatedTime, priority, notes);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete-task")
    public ResponseEntity<Void> deleteTask(@RequestBody Map<String, String> payload, @RequestHeader("x-user-id") String userId) {
        String scheduleId = payload.get("scheduleId");
        String taskId = payload.get("taskId");
        dailyScheduleService.deleteTask(userId, scheduleId, taskId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reorder-tasks")
    public ResponseEntity<Void> reorderTasks(@RequestBody Map<String, Object> payload, @RequestHeader("x-user-id") String userId) {
        String scheduleId = (String) payload.get("scheduleId");
        List<String> taskIds = (List<String>) payload.get("taskIds");
        dailyScheduleService.reorderTasks(userId, scheduleId, taskIds);
        return ResponseEntity.ok().build();
    }
}
