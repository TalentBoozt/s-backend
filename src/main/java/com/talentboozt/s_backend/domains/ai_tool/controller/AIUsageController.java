package com.talentboozt.s_backend.domains.ai_tool.controller;

import com.talentboozt.s_backend.domains.subscription.domain.model.SubscriptionPlanCode;
import com.talentboozt.s_backend.domains.subscription.model.Subscription;
import com.talentboozt.s_backend.domains.subscription.service.SubscriptionService;
import com.talentboozt.s_backend.domains.subscription.infrastructure.mapping.LmsPlanAndStatusMapping;
import com.talentboozt.s_backend.shared.security.annotations.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai/usage")
@RequiredArgsConstructor
public class AIUsageController {

    private final com.talentboozt.s_backend.domains.edu.service.EduAICreditService creditService;
    private final SubscriptionService subscriptionService;

    @GetMapping("/quota")
    public ResponseEntity<java.util.Map<String, Object>> getMyQuota(@AuthenticatedUser String userId) {
        Subscription sub = subscriptionService.getActiveSubscription(userId);
        SubscriptionPlanCode plan = sub != null && sub.getPlan() != null ? sub.getPlan() : SubscriptionPlanCode.FREE;
        var credits = creditService.getQuota(userId, LmsPlanAndStatusMapping.toEduPlan(plan));
        
        return ResponseEntity.ok(java.util.Map.of(
            "used", (credits.getMonthlyLimit() != null ? credits.getMonthlyLimit() : 0) - (credits.getBalance() != null ? credits.getBalance() : 0),
            "monthlyLimit", credits.getMonthlyLimit() != null ? credits.getMonthlyLimit() : 0,
            "remaining", credits.getBalance() != null ? credits.getBalance() : 0,
            "resetDate", credits.getLastResetDate() != null ? credits.getLastResetDate().plus(30, java.time.temporal.ChronoUnit.DAYS) : java.time.Instant.now()
        ));
    }

    @GetMapping("/logs")
    public ResponseEntity<java.util.List<com.talentboozt.s_backend.domains.edu.model.ECreditLedger>> getMyUsageLogs(
            @AuthenticatedUser String userId) {
        return ResponseEntity.ok(creditService.getCreditLedger(userId));
    }
}
