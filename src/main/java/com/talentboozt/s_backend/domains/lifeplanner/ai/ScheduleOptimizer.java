package com.talentboozt.s_backend.domains.lifeplanner.ai;

import org.springframework.stereotype.Service;
import com.talentboozt.s_backend.domains.lifeplanner.user.model.UserPreferences;
import com.talentboozt.s_backend.domains.lifeplanner.user.service.UserService;
import com.talentboozt.s_backend.domains.lifeplanner.ai.model.OptimizedScheduleResponse;
import com.talentboozt.s_backend.domains.lifeplanner.credits.service.LifePlannerCreditService;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleOptimizer {

    private final AIClient aiClient;
    private final PromptBuilder promptBuilder;
    private final LifePlannerCreditService lifePlannerCreditService;
    private final UserService userService;

    public OptimizedScheduleResponse optimizeMissedTasks(String userId, List<String> missedTasks) {
        lifePlannerCreditService.deductCredits(userId, 1);
        UserPreferences prefs = userService.getOrCreatePreferences(userId);
        String prompt = promptBuilder.buildScheduleOptimizationPrompt(missedTasks, prefs);
        return aiClient.optimizeSchedule(userId, missedTasks, prompt);
    }
}
