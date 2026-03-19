package com.talentboozt.s_backend.domains.lifeplanner.goal.service;

import org.springframework.stereotype.Service;
import com.talentboozt.s_backend.domains.lifeplanner.goal.model.Goal;
import com.talentboozt.s_backend.domains.lifeplanner.goal.repository.mongodb.GoalRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;

    public Goal createGoal(Goal goal) {
        goal.setCreatedAt(Instant.now());
        goal.setUpdatedAt(Instant.now());
        goal.setStatus("PENDING");
        return goalRepository.save(goal);
    }

    public List<Goal> getGoalsByUserId(String userId) {
        return goalRepository.findByUserId(userId);
    }

    public Optional<Goal> getGoalById(String goalId) {
        return goalRepository.findById(goalId);
    }

    public Goal updateGoal(String goalId, String userId, Goal updatedGoal) {
        return goalRepository.findById(goalId)
            .filter(g -> g.getUserId().equals(userId))
            .map(goal -> {
                goal.setTitle(updatedGoal.getTitle());
                goal.setDescription(updatedGoal.getDescription());
                goal.setDeadline(updatedGoal.getDeadline());
                goal.setDifficulty(updatedGoal.getDifficulty());
                goal.setType(updatedGoal.getType());
                goal.setTimeline(updatedGoal.getTimeline());
                goal.setUpdatedAt(Instant.now());
                return goalRepository.save(goal);
            }).orElseThrow(() -> new java.util.NoSuchElementException("Goal not found or unauthorized"));
    }

    public void deleteGoal(String goalId, String userId) {
        Goal goal = goalRepository.findById(goalId)
            .filter(g -> g.getUserId().equals(userId))
            .orElseThrow(() -> new java.util.NoSuchElementException("Goal not found or unauthorized"));
        goalRepository.delete(goal);
    }
}
