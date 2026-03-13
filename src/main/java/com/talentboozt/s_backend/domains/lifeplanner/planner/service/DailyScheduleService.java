package com.talentboozt.s_backend.domains.lifeplanner.planner.service;

import org.springframework.stereotype.Service;
import com.talentboozt.s_backend.domains.lifeplanner.user.model.UserPreferences;
import com.talentboozt.s_backend.domains.lifeplanner.user.service.UserService;
import com.talentboozt.s_backend.domains.lifeplanner.planner.model.DailySchedule;
import com.talentboozt.s_backend.domains.lifeplanner.planner.model.StudyPlan;
import com.talentboozt.s_backend.domains.lifeplanner.planner.repository.mongodb.DailyScheduleRepository;
import com.talentboozt.s_backend.domains.lifeplanner.ai.model.PlanResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Instant;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DailyScheduleService {

    private final DailyScheduleRepository dailyScheduleRepository;
    private final UserService userService;

    public void generateSchedulesForPlan(StudyPlan plan, List<PlanResponse.DailyTask> dailyTasks, Instant deadline) {
        UserPreferences prefs = userService.getOrCreatePreferences(plan.getUserId());
        String workStartStr = prefs.getWorkHoursStart() != null ? prefs.getWorkHoursStart() : "09:00";
        LocalTime workStart = LocalTime.parse(workStartStr);
        
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTaskTime = workStart;

        List<DailySchedule> schedulesToSave = new ArrayList<>();

        for (PlanResponse.DailyTask aiTask : dailyTasks) {
            DailySchedule schedule = dailyScheduleRepository
                    .findByUserIdAndScheduleDate(plan.getUserId(), currentDate)
                    .orElse(new DailySchedule());

            if (schedule.getScheduleId() == null) {
                schedule.setUserId(plan.getUserId());
                schedule.setPlanId(plan.getPlanId());
                schedule.setScheduleDate(currentDate);
                schedule.setTasks(new ArrayList<>());
                schedule.setCompleted(false);
            }

            DailySchedule.ScheduleTask task = new DailySchedule.ScheduleTask();
            task.setTaskId(UUID.randomUUID().toString());
            task.setTitle(aiTask.getTitle());
            task.setEstimatedTime(aiTask.getEstimatedTime());
            task.setCategory(aiTask.getCategory());
            task.setStartTime(currentTaskTime.toString());
            
            // Simple duration parsing: "45 min" -> 45
            int durationMins = 60;
            try {
                String durationStr = aiTask.getEstimatedTime().replaceAll("[^0-9]", "");
                if (!durationStr.isEmpty()) durationMins = Integer.parseInt(durationStr);
            } catch (Exception e) {}
            
            currentTaskTime = currentTaskTime.plusMinutes(durationMins);
            task.setEndTime(currentTaskTime.toString());
            task.setCompleted(false);

            schedule.getTasks().add(task);
            schedulesToSave.add(schedule);

            // Add break
            currentTaskTime = currentTaskTime.plusMinutes(prefs.getBreakFrequency() > 0 ? prefs.getBreakFrequency() : 15);

            // If we exceed 8 hours of work or move past workHoursEnd, move to next day
            String workEndStr = prefs.getWorkHoursEnd() != null ? prefs.getWorkHoursEnd() : "17:00";
            LocalTime workEnd = LocalTime.parse(workEndStr);
            
            if (currentTaskTime.isAfter(workEnd)) {
                currentDate = currentDate.plusDays(1);
                currentTaskTime = workStart;
            }
        }

        dailyScheduleRepository.saveAll(schedulesToSave);
    }

    public List<DailySchedule> getSchedulesForPlan(String planId) {
        return dailyScheduleRepository.findByPlanId(planId);
    }

    public DailySchedule getTodaySchedule(String userId) {
        return dailyScheduleRepository.findByUserIdAndScheduleDate(userId, LocalDate.now()).orElse(null);
    }

    public void addTaskToToday(String userId, String title, String category, String estimatedTime) {
        DailySchedule schedule = getTodaySchedule(userId);
        if (schedule == null) {
            // Create a basic schedule if none exists for today
            schedule = new DailySchedule();
            schedule.setUserId(userId);
            schedule.setScheduleDate(LocalDate.now());
            schedule.setTasks(new ArrayList<>());
            schedule.setCompleted(false);
        }

        DailySchedule.ScheduleTask task = new DailySchedule.ScheduleTask();
        task.setTaskId(UUID.randomUUID().toString());
        task.setTitle(title);
        task.setCategory(category);
        task.setEstimatedTime(estimatedTime);
        task.setCompleted(false);
        
        // Find last task end time or start at 9 AM
        LocalTime startTime = LocalTime.of(9, 0);
        if (!schedule.getTasks().isEmpty()) {
            DailySchedule.ScheduleTask last = schedule.getTasks().get(schedule.getTasks().size() - 1);
            if (last.getEndTime() != null) {
                startTime = LocalTime.parse(last.getEndTime()).plusMinutes(15);
            }
        }
        task.setStartTime(startTime.toString());
        task.setEndTime(startTime.plusMinutes(45).toString());

        schedule.getTasks().add(task);
        dailyScheduleRepository.save(schedule);
    }

    public void reorderTasks(String userId, String scheduleId, List<String> taskIds) {
        DailySchedule schedule = dailyScheduleRepository.findById(scheduleId).orElse(null);
        if (schedule != null && schedule.getUserId().equals(userId)) {
            List<DailySchedule.ScheduleTask> reordered = new ArrayList<>();
            for (String tid : taskIds) {
                schedule.getTasks().stream()
                    .filter(t -> t.getTaskId().equals(tid))
                    .findFirst()
                    .ifPresent(reordered::add);
            }
            // Add any missing tasks that weren't in the taskId list
            schedule.getTasks().stream()
                .filter(t -> !taskIds.contains(t.getTaskId()))
                .forEach(reordered::add);
                
            schedule.setTasks(reordered);
            dailyScheduleRepository.save(schedule);
        }
    }

    public DailySchedule save(DailySchedule schedule) {
        return dailyScheduleRepository.save(schedule);
    }
}
