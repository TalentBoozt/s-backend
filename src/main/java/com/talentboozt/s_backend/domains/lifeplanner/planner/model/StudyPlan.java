package com.talentboozt.s_backend.domains.lifeplanner.planner.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.List;
import com.talentboozt.s_backend.domains.lifeplanner.ai.model.PlanResponse;

@Data
@Document(collection = "lp_study_plans")
public class StudyPlan {
    @Id
    private String planId;
    @Indexed
    private String goalId;
    @Indexed
    private String userId;
    @Indexed
    private String status; // ACTIVE, COMPLETED, ARCHIVED, STALE
    private List<PlanResponse.RoadmapItem> roadmap;
    private List<PlanResponse.WeeklyPlan> weeklyPlans;
    private Instant createdAt;
    private Instant updatedAt;
    private double progressPercentage;
    private List<Integer> reachedMilestones = new java.util.ArrayList<>();
}
