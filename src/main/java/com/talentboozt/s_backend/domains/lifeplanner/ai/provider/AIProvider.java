package com.talentboozt.s_backend.domains.lifeplanner.ai.provider;

import com.talentboozt.s_backend.domains.lifeplanner.goal.model.Goal;
import com.talentboozt.s_backend.domains.lifeplanner.user.model.UserProfile;
import com.talentboozt.s_backend.domains.lifeplanner.ai.model.PlanResponse;
import com.talentboozt.s_backend.domains.lifeplanner.ai.model.OptimizedScheduleResponse;
import java.util.List;

public interface AIProvider {
    PlanResponse generatePlan(Goal goal, UserProfile userProfile, String prompt);
    String generateJournalPrompt(UserProfile userProfile, String prompt);
    OptimizedScheduleResponse optimizeSchedule(List<String> missedTasks, String prompt);
}
