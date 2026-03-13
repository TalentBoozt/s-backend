package com.talentboozt.s_backend.domains.lifeplanner.planner.service;

import org.springframework.stereotype.Service;
import com.talentboozt.s_backend.domains.lifeplanner.planner.model.DailySchedule;
import com.talentboozt.s_backend.domains.lifeplanner.planner.repository.mongodb.DailyScheduleRepository;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskCompletionService {

    private final DailyScheduleRepository dailyScheduleRepository;
    private final StudyPlanService studyPlanService;

    public void markTaskAsCompleted(String userId, String scheduleId, String taskId) {
        Optional<DailySchedule> optSchedule = dailyScheduleRepository.findById(scheduleId);
        if (optSchedule.isPresent()) {
            DailySchedule schedule = optSchedule.get();
            if (!schedule.getUserId().equals(userId)) {
                throw new SecurityException("Not authorized to modify this schedule");
            }
            boolean allCompleted = true;
            boolean found = false;
            
            if (schedule.getTasks() != null) {
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
            }

            if (found) {
                schedule.setCompleted(allCompleted);
                dailyScheduleRepository.save(schedule);
                studyPlanService.updatePlanProgress(schedule.getPlanId());
            }
        }
    }
}
