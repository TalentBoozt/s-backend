package com.talentboozt.s_backend.domains.ai_tool.scheduler;

import com.talentboozt.s_backend.domains.ai_tool.service.AIUsageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AIQuotaScheduler {

    private final AIUsageService aiUsageService;

    // Run at 00:00 on the first day of every month
    @Scheduled(cron = "0 0 0 1 * *")
    public void resetMonthlyQuotas() {
        log.info("Cron Job: Starting monthly AI quota reset");
        aiUsageService.resetAllMonthlyQuotas();
    }
}
