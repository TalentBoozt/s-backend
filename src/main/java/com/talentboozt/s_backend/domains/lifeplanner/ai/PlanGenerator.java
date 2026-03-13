package com.talentboozt.s_backend.domains.lifeplanner.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.talentboozt.s_backend.domains.lifeplanner.goal.model.Goal;
import com.talentboozt.s_backend.domains.lifeplanner.user.model.UserProfile;
import com.talentboozt.s_backend.domains.lifeplanner.user.model.UserPreferences;
import com.talentboozt.s_backend.domains.lifeplanner.user.service.UserService;
import com.talentboozt.s_backend.domains.lifeplanner.ai.model.PlanResponse;
import com.talentboozt.s_backend.domains.lifeplanner.ai.cache.AICacheService;
import com.talentboozt.s_backend.domains.lifeplanner.credits.service.LifePlannerCreditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanGenerator {

    private final AIClient aiClient;
    private final PromptBuilder promptBuilder;
    private final AICacheService aiCacheService;
    private final LifePlannerCreditService lifePlannerCreditService;
    private final UserService userService;

    @Value("${lifeplanner.ai.provider:GEMINI}")
    private String activeProvider;

    public PlanResponse generatePlanForGoal(Goal goal, UserProfile profile) {
        UserPreferences prefs = userService.getOrCreatePreferences(goal.getUserId());
        String prompt = promptBuilder.buildPlanGenerationPrompt(goal, profile, prefs);

        // Check cache first
        Optional<PlanResponse> cached = aiCacheService.getCachedPlan(prompt, activeProvider);
        if (cached.isPresent()) {
            log.info("Returning cached AI plan for goal: {}", goal.getGoalId());
            return cached.get();
        }

        // Deduct 1 credit for generating a new plan
        lifePlannerCreditService.deductCredits(goal.getUserId(), 1);

        // Call LLM
        PlanResponse response = aiClient.generatePlan(goal, profile, prompt);

        // Cache the result
        aiCacheService.cachePlan(prompt, activeProvider, response);

        return response;
    }
}
