package com.talentboozt.s_backend.domains.lifeplanner.goal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.talentboozt.s_backend.domains.lifeplanner.goal.model.Goal;
import com.talentboozt.s_backend.domains.lifeplanner.goal.service.GoalService;
import com.talentboozt.s_backend.domains.lifeplanner.goal.dto.GoalResponseDTO;
import com.talentboozt.s_backend.domains.lifeplanner.planner.service.StudyPlanService;
import com.talentboozt.s_backend.domains.lifeplanner.planner.model.StudyPlan;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/lifeplanner/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;
    private final StudyPlanService studyPlanService;

    @PostMapping
    public ResponseEntity<Goal> createGoal(@RequestBody Goal goal, @RequestHeader("x-user-id") String userId) {
        goal.setUserId(userId);
        Goal createdGoal = goalService.createGoal(goal);
        return ResponseEntity.ok(createdGoal);
    }

    @GetMapping
    public ResponseEntity<List<GoalResponseDTO>> getGoals(@RequestHeader("x-user-id") String userId) {
        List<Goal> goals = goalService.getGoalsByUserId(userId);
        List<GoalResponseDTO> responses = new ArrayList<>();
        
        for (Goal goal : goals) {
            Optional<StudyPlan> plan = studyPlanService.getPlanByGoalId(goal.getGoalId());
            String planStatus = plan.map(StudyPlan::getStatus).orElse("NO_PLAN");
            double progress = plan.map(StudyPlan::getProgressPercentage).orElse(0.0);
            responses.add(GoalResponseDTO.fromEntity(goal, planStatus, progress));
        }
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoalResponseDTO> getGoalById(@PathVariable String id, @RequestHeader("x-user-id") String userId) {
        Goal goal = goalService.getGoalById(id)
                .filter(g -> g.getUserId().equals(userId))
                .orElseThrow(() -> new NoSuchElementException("Goal not found"));

        Optional<StudyPlan> plan = studyPlanService.getPlanByGoalId(goal.getGoalId());
        String planStatus = plan.map(StudyPlan::getStatus).orElse("NO_PLAN");
        double progress = plan.map(StudyPlan::getProgressPercentage).orElse(0.0);
        
        return ResponseEntity.ok(GoalResponseDTO.fromEntity(goal, planStatus, progress));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoalResponseDTO> updateGoal(@PathVariable String id, @RequestBody Goal goal, @RequestHeader("x-user-id") String userId) {
        Goal updatedGoal = goalService.updateGoal(id, userId, goal);
        
        Optional<StudyPlan> plan = studyPlanService.getPlanByGoalId(updatedGoal.getGoalId());
        String planStatus = plan.map(StudyPlan::getStatus).orElse("NO_PLAN");
        double progress = plan.map(StudyPlan::getProgressPercentage).orElse(0.0);

        return ResponseEntity.ok(GoalResponseDTO.fromEntity(updatedGoal, planStatus, progress));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable String id, @RequestHeader("x-user-id") String userId) {
        studyPlanService.deletePlanByGoalId(id);
        goalService.deleteGoal(id, userId);
        return ResponseEntity.noContent().build();
    }
}
