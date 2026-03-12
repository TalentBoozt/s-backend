package com.talentboozt.s_backend.domains.lifeplanner.planner.service;

import org.springframework.stereotype.Service;
import com.talentboozt.s_backend.domains.lifeplanner.planner.model.DailySchedule;
import com.talentboozt.s_backend.domains.lifeplanner.planner.model.StudyPlan;
import com.talentboozt.s_backend.domains.lifeplanner.planner.repository.mongodb.DailyScheduleRepository;
import com.talentboozt.s_backend.domains.lifeplanner.planner.repository.mongodb.StudyPlanRepository;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskCompletionService {

    private final DailyScheduleRepository dailyScheduleRepository;
    private final StudyPlanRepository studyPlanRepository;

    public void markTaskAsCompleted(String userId, String scheduleId, String taskId) {
        Optional<DailySchedule> optSchedule = dailyScheduleRepository.findById(scheduleId);
        if (optSchedule.isPresent()) {
            DailySchedule schedule = optSchedule.get();
            if (!schedule.getUserId().equals(userId)) {
                throw new SecurityException("Not authorized to modify this schedule");
            }
            boolean allCompleted = true;
            boolean found = false;
            
            for (DailySchedule.ScheduleTask task : schedule.getTasks()) {
                if (task.getTaskId().equals(taskId)) {
                    task.setCompleted(true);
                    task.setCompletedAt(Instant.now().toString());
                    found = true;
                }
                if (!task.isCompleted()) {
                    allCompleted = false;
                }
            }

            if (found) {
                schedule.setCompleted(allCompleted);
                dailyScheduleRepository.save(schedule);
                updatePlanProgress(schedule.getPlanId());
            }
        }
    }

    private void updatePlanProgress(String planId) {
        // Recalculate progress across all schedules for this plan
        Optional<StudyPlan> optPlan = studyPlanRepository.findById(planId);
        if (optPlan.isPresent()) {
            StudyPlan plan = optPlan.get();
            long totalTasks = 0;
            long completedTasks = 0;

            for (DailySchedule schedule : dailyScheduleRepository.findByPlanId(planId)) {
                totalTasks += schedule.getTasks().size();
                completedTasks += schedule.getTasks().stream().filter(DailySchedule.ScheduleTask::isCompleted).count();
            }

            if (totalTasks > 0) {
                double progress = (double) completedTasks / totalTasks * 100.0;
                plan.setProgressPercentage(progress);
                if (progress >= 100.0) {
                    plan.setStatus("COMPLETED");
                }
                studyPlanRepository.save(plan);
            }
        }
    }
}
