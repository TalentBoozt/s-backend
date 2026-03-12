package com.talentboozt.s_backend.domains.lifeplanner.planner.service;

import org.springframework.stereotype.Service;
import com.talentboozt.s_backend.domains.lifeplanner.planner.model.StudyPlan;
import com.talentboozt.s_backend.domains.lifeplanner.planner.repository.mongodb.StudyPlanRepository;
import com.talentboozt.s_backend.domains.lifeplanner.ai.model.PlanResponse;
import com.talentboozt.s_backend.domains.lifeplanner.goal.model.Goal;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudyPlanService {

    private final StudyPlanRepository studyPlanRepository;
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
}
