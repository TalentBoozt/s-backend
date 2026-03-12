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
}
