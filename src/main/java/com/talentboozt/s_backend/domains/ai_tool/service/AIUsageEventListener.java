package com.talentboozt.s_backend.domains.ai_tool.service;

import com.talentboozt.s_backend.domains.subscription.event.UserPlanChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AIUsageEventListener {

    private final AIUsageService aiUsageService;

    @EventListener
    public void handleUserPlanChanged(UserPlanChangedEvent event) {
        log.info("Received UserPlanChangedEvent for user: {}, new plan: {}", event.getUserId(), event.getNewPlan());
        aiUsageService.updateQuotaForPlan(event.getUserId(), event.getNewPlan());
    }
}
