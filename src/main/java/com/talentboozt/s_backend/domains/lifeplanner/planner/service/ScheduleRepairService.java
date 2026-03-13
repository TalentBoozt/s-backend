package com.talentboozt.s_backend.domains.lifeplanner.planner.service;

import org.springframework.stereotype.Service;
import com.talentboozt.s_backend.domains.lifeplanner.planner.model.DailySchedule;
import com.talentboozt.s_backend.domains.lifeplanner.planner.model.StudyPlan;
import com.talentboozt.s_backend.domains.lifeplanner.planner.repository.mongodb.DailyScheduleRepository;
import com.talentboozt.s_backend.domains.lifeplanner.planner.repository.mongodb.StudyPlanRepository;
import com.talentboozt.s_backend.domains.lifeplanner.ai.ScheduleOptimizer;
import com.talentboozt.s_backend.domains.lifeplanner.ai.model.OptimizedScheduleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleRepairService {

    private final DailyScheduleRepository dailyScheduleRepository;
    private final StudyPlanRepository studyPlanRepository;
    private final ScheduleOptimizer scheduleOptimizer;

    public int getMissedTaskCountByPlan(String planId) {
        LocalDate today = LocalDate.now();
        List<DailySchedule> pastSchedules = dailyScheduleRepository.findByPlanId(planId).stream()
                .filter(schedule -> schedule.getScheduleDate().isBefore(today) && !schedule.isCompleted())
                .collect(Collectors.toList());

        int count = 0;
        for (DailySchedule schedule : pastSchedules) {
            for (DailySchedule.ScheduleTask task : schedule.getTasks()) {
                if (!task.isCompleted()) {
                    count++;
                }
            }
        }
        return count;
    }

    public OptimizedScheduleResponse repairSchedule(String planId, String requestUserId) {
        LocalDate today = LocalDate.now();

        // 1. Find all uncompleted tasks from past days
        List<DailySchedule> pastSchedules = dailyScheduleRepository.findByPlanId(planId).stream()
                .filter(schedule -> schedule.getScheduleDate().isBefore(today) && !schedule.isCompleted())
                .collect(Collectors.toList());

        List<String> missedTasks = new ArrayList<>();

        for (DailySchedule schedule : pastSchedules) {
            for (DailySchedule.ScheduleTask task : schedule.getTasks()) {
                if (!task.isCompleted()) {
                    missedTasks.add(task.getTitle() + " (" + task.getEstimatedTime() + ")");
                }
            }
        }

        if (missedTasks.isEmpty()) {
            OptimizedScheduleResponse noAction = new OptimizedScheduleResponse();
            noAction.setRationale("No missed tasks found. Schedule is optimal.");
            noAction.setRescheduledTasks(new ArrayList<>());
            return noAction;
        }

        StudyPlan plan = studyPlanRepository.findById(planId).orElse(null);
        String userId = plan != null ? plan.getUserId() : null;

        if (userId == null || (requestUserId != null && !userId.equals(requestUserId))) {
            throw new SecurityException("Unknown plan or unauthorized user.");
        }

        // 2. Call AI to figure out how to redistribute
        OptimizedScheduleResponse aiResult = scheduleOptimizer.optimizeMissedTasks(userId, missedTasks);
        aiResult.setMissedTasks(missedTasks);

        // 3. Write the rescheduled tasks back into future DailySchedules
        if (aiResult.getRescheduledTasks() != null) {

            for (OptimizedScheduleResponse.RescheduledTask rTask : aiResult.getRescheduledTasks()) {
                LocalDate assignedDate;
                try {
                    assignedDate = LocalDate.parse(rTask.getAssignedDay());
                } catch (Exception e) {
                    // If AI returned an unparseable date, assign to tomorrow
                    assignedDate = today.plusDays(1);
                }

                if (userId == null) continue;

                DailySchedule targetSchedule = dailyScheduleRepository
                        .findByUserIdAndScheduleDate(userId, assignedDate)
                        .orElse(new DailySchedule());

                if (targetSchedule.getScheduleId() == null) {
                    targetSchedule.setUserId(userId);
                    targetSchedule.setPlanId(planId);
                    targetSchedule.setScheduleDate(assignedDate);
                    targetSchedule.setTasks(new ArrayList<>());
                    targetSchedule.setCompleted(false);
                }

                DailySchedule.ScheduleTask newTask = new DailySchedule.ScheduleTask();
                newTask.setTaskId(UUID.randomUUID().toString());
                newTask.setTitle(rTask.getTitle());
                newTask.setEstimatedTime(rTask.getEstimatedTime());
                newTask.setCategory(rTask.getCategory());
                newTask.setCompleted(false);

                targetSchedule.getTasks().add(newTask);
                dailyScheduleRepository.save(targetSchedule);
            }

            log.info("Written {} rescheduled tasks into future daily schedules for plan: {}",
                    aiResult.getRescheduledTasks().size(), planId);
        }

        return aiResult;
    }
}
