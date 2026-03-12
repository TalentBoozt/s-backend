package com.talentboozt.s_backend.domains.lifeplanner.goal.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data
@Document(collection = "lp_goals")
public class Goal {
    @Id
    private String goalId;
    @Indexed
    private String userId;
    private String title;
    private String description;
    private Instant deadline;
    private String difficulty;
    private String status;
    private GoalType type;
    private GoalTimeline timeline;
    private Instant createdAt;
    private Instant updatedAt;
}
