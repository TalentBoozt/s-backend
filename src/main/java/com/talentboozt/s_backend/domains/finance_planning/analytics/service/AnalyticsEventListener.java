package com.talentboozt.s_backend.domains.finance_planning.analytics.service;

import com.talentboozt.s_backend.domains.finance_planning.events.FinancialsChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyticsEventListener {
    private final AnalyticsPrecomputationService precomputationService;

    @Async
    @EventListener
    public void handleFinancialsChanged(FinancialsChangedEvent event) {
        log.info("Financials changed for project: {}. Triggering precomputation...", event.getProjectId());
        precomputationService.precomputeAll(event.getOrganizationId(), event.getProjectId(), event.getScenarioId());
        log.info("Precomputation completed for project: {}", event.getProjectId());
    }
}
