package com.talentboozt.s_backend.domains.lifeplanner.planner.service;

import org.springframework.stereotype.Service;
import com.talentboozt.s_backend.domains.lifeplanner.planner.model.StudyPlan;
import com.talentboozt.s_backend.domains.lifeplanner.planner.repository.mongodb.StudyPlanRepository;
import com.talentboozt.s_backend.domains.lifeplanner.ai.model.PlanResponse;
import com.talentboozt.s_backend.domains.lifeplanner.goal.model.Goal;
import com.talentboozt.s_backend.domains.lifeplanner.planner.repository.mongodb.DailyScheduleRepository;
import com.talentboozt.s_backend.domains.lifeplanner.planner.model.DailySchedule;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import java.util.Optional;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudyPlanService {

    private final StudyPlanRepository studyPlanRepository;
    private final DailyScheduleRepository dailyScheduleRepository;
    private final DailyScheduleService dailyScheduleService;

    // This acts as the PlannerEngine taking AI output and persisting it
    public StudyPlan createPlanFromAIResponse(Goal goal, PlanResponse planResponse) {
        StudyPlan plan = new StudyPlan();
        plan.setGoalId(goal.getGoalId());
        plan.setUserId(goal.getUserId());
        plan.setRoadmap(planResponse.getRoadmap());
        plan.setWeeklyPlans(planResponse.getWeeklyPlans());
        plan.setCreatedAt(Instant.now());
        plan.setUpdatedAt(Instant.now());
        plan.setStatus("ACTIVE");
        plan.setProgressPercentage(0.0);

        StudyPlan savedPlan = studyPlanRepository.save(plan);

        // Generate daily schedules based on daily tasks
        dailyScheduleService.generateSchedulesForPlan(savedPlan, planResponse.getDailyTasks(), goal.getDeadline());

        return savedPlan;
    }

    public Optional<StudyPlan> getPlanByGoalId(String goalId) {
        return studyPlanRepository.findByGoalId(goalId);
    }

    public StudyPlan save(StudyPlan plan) {
        plan.setUpdatedAt(Instant.now());
        return studyPlanRepository.save(plan);
    }

    public void updatePlanProgress(String planId) {
        Optional<StudyPlan> optPlan = studyPlanRepository.findById(planId);
        if (optPlan.isPresent()) {
            StudyPlan plan = optPlan.get();
            List<DailySchedule> schedules = dailyScheduleRepository.findByPlanId(planId);
            
            long totalTasks = 0;
            long completedTasks = 0;

            for (DailySchedule schedule : schedules) {
                if (schedule.getTasks() != null) {
                    totalTasks += schedule.getTasks().size();
                    completedTasks += schedule.getTasks().stream()
                            .filter(DailySchedule.ScheduleTask::isCompleted)
                            .count();
                }
            }

            if (totalTasks > 0) {
                double progress = (double) completedTasks / totalTasks * 100.0;
                plan.setProgressPercentage(progress);
                
                // Check milestones
                int[] milestones = {25, 50, 75};
                for (int m : milestones) {
                    if (progress >= m && !plan.getReachedMilestones().contains(m)) {
                        plan.getReachedMilestones().add(m);
                        log.info("User {} reached milestone {}% for plan {}", plan.getUserId(), m, planId);
                    }
                }

                if (progress >= 100.0) {
                    plan.setStatus("COMPLETED");
                } else if (plan.getUpdatedAt().isBefore(Instant.now().minus(java.time.Duration.ofDays(7)))) {
                    plan.setStatus("STALE");
                }
                plan.setUpdatedAt(Instant.now());
                studyPlanRepository.save(plan);
            }
        }
    }

    public Optional<StudyPlan> findById(String planId) {
        return studyPlanRepository.findById(planId);
    }

    public List<StudyPlan> findByUserId(String userId) {
        return studyPlanRepository.findByUserId(userId);
    }
}
