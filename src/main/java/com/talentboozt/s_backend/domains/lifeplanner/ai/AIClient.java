package com.talentboozt.s_backend.domains.lifeplanner.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.talentboozt.s_backend.domains.lifeplanner.ai.provider.AIProvider;
import com.talentboozt.s_backend.domains.lifeplanner.ai.provider.OpenAIProvider;
import com.talentboozt.s_backend.domains.lifeplanner.ai.provider.GeminiProvider;
import com.talentboozt.s_backend.domains.lifeplanner.goal.model.Goal;
import com.talentboozt.s_backend.domains.lifeplanner.user.model.UserProfile;
import com.talentboozt.s_backend.domains.lifeplanner.ai.model.PlanResponse;
import com.talentboozt.s_backend.domains.lifeplanner.ai.model.OptimizedScheduleResponse;
import com.talentboozt.s_backend.domains.lifeplanner.credits.model.SubscriptionTier;
import com.talentboozt.s_backend.domains.lifeplanner.credits.model.UserCredits;
import com.talentboozt.s_backend.domains.lifeplanner.credits.service.LifePlannerCreditService;
import java.util.List;

@Service
public class AIClient {

    private final OpenAIProvider openAIProvider;
    private final GeminiProvider geminiProvider;
    private final LifePlannerCreditService lifePlannerCreditService;
    private final String defaultProvider;

    public AIClient(OpenAIProvider openAIProvider, 
                    GeminiProvider geminiProvider, 
                    LifePlannerCreditService lifePlannerCreditService,
                    @Value("${lifeplanner.ai.provider:OPENAI}") String defaultProvider) {
        this.openAIProvider = openAIProvider;
        this.geminiProvider = geminiProvider;
        this.lifePlannerCreditService = lifePlannerCreditService;
        this.defaultProvider = defaultProvider;
    }

    private AIProvider getActiveProviderForUser(String userId) {
        if (userId != null) {
            UserCredits credits = lifePlannerCreditService.getUserCreditsInfo(userId);
            if (credits.getTier() == SubscriptionTier.FREE) {
                // Free users only get Gemini
                return geminiProvider;
            }
        }
        
        // Premium/Pro users get the configured default provider
        if ("GEMINI".equalsIgnoreCase(defaultProvider)) {
            return geminiProvider;
        }
        return openAIProvider;
    }

    public PlanResponse generatePlan(Goal goal, UserProfile userProfile, String prompt) {
        return getActiveProviderForUser(userProfile.getUserId()).generatePlan(goal, userProfile, prompt);
    }

    public String generateJournalPrompt(UserProfile userProfile, String prompt) {
        return getActiveProviderForUser(userProfile.getUserId()).generateJournalPrompt(userProfile, prompt);
    }
    
    public OptimizedScheduleResponse optimizeSchedule(String userId, List<String> missedTasks, String prompt) {
        return getActiveProviderForUser(userId).optimizeSchedule(missedTasks, prompt);
    }
}
