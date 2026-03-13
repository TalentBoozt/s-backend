package com.talentboozt.s_backend.domains.lifeplanner.goal.dto;

import lombok.Data;
import java.time.Instant;
import com.talentboozt.s_backend.domains.lifeplanner.goal.model.Goal;

@Data
public class GoalResponseDTO {
    private String goalId;
    private String userId;
    private String title;
    private String description;
    private Instant deadline;
    private String difficulty;
    private String status;
    private String planStatus; // ACTIVE, STALE, COMPLETED, NO_PLAN
    private double progressPercentage;

    public static GoalResponseDTO fromEntity(Goal goal, String planStatus, double progress) {
        GoalResponseDTO dto = new GoalResponseDTO();
        dto.setGoalId(goal.getGoalId());
        dto.setUserId(goal.getUserId());
        dto.setTitle(goal.getTitle());
        dto.setDescription(goal.getDescription());
        dto.setDeadline(goal.getDeadline());
        dto.setDifficulty(goal.getDifficulty());
        dto.setStatus(goal.getStatus());
        dto.setPlanStatus(planStatus);
        dto.setProgressPercentage(progress);
        return dto;
    }
}
