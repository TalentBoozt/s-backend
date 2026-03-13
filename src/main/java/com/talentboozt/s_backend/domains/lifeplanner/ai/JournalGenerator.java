package com.talentboozt.s_backend.domains.lifeplanner.ai;

import org.springframework.stereotype.Service;
import com.talentboozt.s_backend.domains.lifeplanner.credits.service.LifePlannerCreditService;
import com.talentboozt.s_backend.domains.lifeplanner.user.model.UserPreferences;
import com.talentboozt.s_backend.domains.lifeplanner.user.service.UserService;
import com.talentboozt.s_backend.domains.lifeplanner.user.model.UserProfile;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JournalGenerator {

    private final AIClient aiClient;
    private final PromptBuilder promptBuilder;
    private final LifePlannerCreditService lifePlannerCreditService;
    private final UserService userService;

    public String generateDailyPrompt(UserProfile profile) {
        lifePlannerCreditService.deductCredits(profile.getUserId(), 1);
        UserPreferences prefs = userService.getOrCreatePreferences(profile.getUserId());
        String prompt = promptBuilder.buildJournalPrompt(profile, prefs);
        return aiClient.generateJournalPrompt(profile, prompt);
    }

    public String generateInsight(String userId, String reflection) {
        lifePlannerCreditService.deductCredits(userId, 2); // Insights are more expensive
        UserProfile profile = userService.getProfileByUserId(userId).orElse(new UserProfile());
        UserPreferences prefs = userService.getOrCreatePreferences(userId);
        return aiClient.generateJournalInsight(profile, reflection, prefs);
    }
}
