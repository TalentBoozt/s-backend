package com.talentboozt.s_backend.domains.ai_tool.controller;

import com.talentboozt.s_backend.domains.edu.model.EAiUsage;
import com.talentboozt.s_backend.shared.security.annotations.AuthenticatedUser;
import com.talentboozt.s_backend.shared.security.model.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai/usage")
@RequiredArgsConstructor
public class AIUsageController {

    private final com.talentboozt.s_backend.domains.edu.service.EduAICreditService creditService;
    private final com.talentboozt.s_backend.domains.subscription.service.SubscriptionService subscriptionService;

    @GetMapping("/quota")
    public ResponseEntity<java.util.Map<String, Object>> getMyQuota(@AuthenticatedUser String userId) {
        var sub = subscriptionService.getActiveSubscription(userId);
        var plan = sub != null ? sub.getPlan() : com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan.FREE;
        var credits = creditService.getQuota(userId, plan);
        
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
