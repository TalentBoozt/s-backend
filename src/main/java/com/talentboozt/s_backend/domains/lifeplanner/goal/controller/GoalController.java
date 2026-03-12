package com.talentboozt.s_backend.domains.lifeplanner.goal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.talentboozt.s_backend.domains.lifeplanner.goal.model.Goal;
import com.talentboozt.s_backend.domains.lifeplanner.goal.service.GoalService;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lifeplanner/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @PostMapping
    public ResponseEntity<Goal> createGoal(@RequestBody Goal goal, @RequestHeader("x-user-id") String userId) {
        goal.setUserId(userId);
        Goal createdGoal = goalService.createGoal(goal);
        return ResponseEntity.ok(createdGoal);
    }

    @GetMapping
    public ResponseEntity<List<Goal>> getGoals(@RequestHeader("x-user-id") String userId) {
        List<Goal> goals = goalService.getGoalsByUserId(userId);
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Goal> getGoalById(@PathVariable String id, @RequestHeader("x-user-id") String userId) {
        return goalService.getGoalById(id)
                .filter(goal -> goal.getUserId().equals(userId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(403).build());
    }
}
