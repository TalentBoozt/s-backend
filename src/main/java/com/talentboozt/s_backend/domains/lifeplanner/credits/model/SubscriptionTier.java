package com.talentboozt.s_backend.domains.lifeplanner.credits.model;

public enum SubscriptionTier {
    FREE(15),       // Limited free credits per month
    PRO(100),       // 100 credits
    PREMIUM(1000);  // High usage + OpenAI access

    private final int monthlyCredits;

    SubscriptionTier(int monthlyCredits) {
        this.monthlyCredits = monthlyCredits;
    }

    public int getMonthlyCredits() {
        return monthlyCredits;
    }
}
