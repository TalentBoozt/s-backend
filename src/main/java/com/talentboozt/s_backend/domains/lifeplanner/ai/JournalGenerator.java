package com.talentboozt.s_backend.domains.lifeplanner.ai;

import org.springframework.stereotype.Service;
import com.talentboozt.s_backend.domains.lifeplanner.credits.service.LifePlannerCreditService;
import com.talentboozt.s_backend.domains.lifeplanner.user.model.UserProfile;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JournalGenerator {

    private final AIClient aiClient;
    private final PromptBuilder promptBuilder;
    private final LifePlannerCreditService lifePlannerCreditService;

    public String generateDailyPrompt(UserProfile profile) {
        lifePlannerCreditService.deductCredits(profile.getUserId(), 1);
        String prompt = promptBuilder.buildJournalPrompt(profile);
        return aiClient.generateJournalPrompt(profile, prompt);
    }
}
