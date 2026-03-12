package com.talentboozt.s_backend.domains.lifeplanner.planner.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.talentboozt.s_backend.domains.lifeplanner.planner.model.StudyPlan;
import com.talentboozt.s_backend.domains.lifeplanner.planner.service.StudyPlanService;
import com.talentboozt.s_backend.domains.lifeplanner.ai.PlanGenerator;
import com.talentboozt.s_backend.domains.lifeplanner.goal.service.GoalService;
import com.talentboozt.s_backend.domains.lifeplanner.user.service.UserService;
import com.talentboozt.s_backend.domains.lifeplanner.goal.model.Goal;
import com.talentboozt.s_backend.domains.lifeplanner.user.model.UserProfile;
import com.talentboozt.s_backend.domains.lifeplanner.ai.model.PlanResponse;
import com.talentboozt.s_backend.domains.lifeplanner.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import java.util.Map;

@RestController
@RequestMapping("/api/lifeplanner/plans")
@RequiredArgsConstructor
public class PlanController {

    private final StudyPlanService studyPlanService;
    private final PlanGenerator planGenerator;
    private final GoalService goalService;
    private final UserService userService;

    @PostMapping("/generate")
    @io.github.resilience4j.ratelimiter.annotation.RateLimiter(name = "planGeneration")
    public ResponseEntity<StudyPlan> generatePlan(@RequestBody Map<String, String> payload, @RequestHeader("x-user-id") String userId) {
        String goalId = payload.get("goalId");
        Goal goal = goalService.getGoalById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found: " + goalId));
        UserProfile profile = userService.getProfileByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user: " + userId));

        PlanResponse response = planGenerator.generatePlanForGoal(goal, profile);
        StudyPlan studyPlan = studyPlanService.createPlanFromAIResponse(goal, response);

        return ResponseEntity.ok(studyPlan);
    }

    @GetMapping("/{goalId}")
    public ResponseEntity<StudyPlan> getPlan(@PathVariable String goalId, @RequestHeader("x-user-id") String userId) {
        StudyPlan plan = studyPlanService.getPlanByGoalId(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found for goal: " + goalId));

        if (!plan.getUserId().equals(userId)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(plan);
    }
}
