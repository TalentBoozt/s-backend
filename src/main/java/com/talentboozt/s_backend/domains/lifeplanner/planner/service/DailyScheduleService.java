package com.talentboozt.s_backend.domains.lifeplanner.planner.service;

import org.springframework.stereotype.Service;
import com.talentboozt.s_backend.domains.lifeplanner.planner.model.DailySchedule;
import com.talentboozt.s_backend.domains.lifeplanner.planner.model.StudyPlan;
import com.talentboozt.s_backend.domains.lifeplanner.planner.repository.mongodb.DailyScheduleRepository;
import com.talentboozt.s_backend.domains.lifeplanner.ai.model.PlanResponse;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DailyScheduleService {

    private final DailyScheduleRepository dailyScheduleRepository;

    public void generateSchedulesForPlan(StudyPlan plan, List<PlanResponse.DailyTask> dailyTasks, Instant deadline) {
        // Logic to distribute tasks over days from now until deadline
        // For simplicity, assigning tasks to subsequent days
        LocalDate currentDate = LocalDate.now();
        
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
            task.setCompleted(false);

            schedule.getTasks().add(task);
            schedulesToSave.add(schedule);
            
            // Advance by one day for the next task as a naive distribution
            currentDate = currentDate.plusDays(1);
        }

        dailyScheduleRepository.saveAll(schedulesToSave);
    }
    
    public List<DailySchedule> getSchedulesForPlan(String planId) {
        return dailyScheduleRepository.findByPlanId(planId);
    }
    
    public DailySchedule getTodaySchedule(String userId) {
        return dailyScheduleRepository.findByUserIdAndScheduleDate(userId, LocalDate.now()).orElse(null);
    }
    
    public DailySchedule save(DailySchedule schedule) {
        return dailyScheduleRepository.save(schedule);
    }
}
