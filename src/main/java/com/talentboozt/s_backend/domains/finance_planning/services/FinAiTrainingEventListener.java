package com.talentboozt.s_backend.domains.finance_planning.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talentboozt.s_backend.domains.finance_planning.dtos.FinanceStateDto;
import com.talentboozt.s_backend.domains.finance_planning.events.FinancialsChangedEvent;
import com.talentboozt.s_backend.domains.finance_planning.models.FinAiTrainingSnapshot;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.FinAiTrainingSnapshotRepository;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.FinFinancialSnapshotRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinAiTrainingEventListener {

    private final FinAiTrainingSnapshotRepository aiTrainingSnapshotRepository;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;
    private final FinFinancialSnapshotRepository financialSnapshotRepository;
    private final com.talentboozt.s_backend.domains.finance_planning.scenario.resolver.ScenarioResolver scenarioResolver;

    @Async
    @EventListener
    public void handleFinancialsChanged(FinancialsChangedEvent event) {
        log.info("Capturing structured AI training snapshot for project: {}, scenario: {}",
                event.getProjectId(), event.getScenarioId());

        try {
            // 1. Capture Inputs (Effective State)
            com.talentboozt.s_backend.domains.finance_planning.scenario.resolver.ScenarioResolver.EffectiveProjectState inputState = scenarioResolver
                    .resolveState(event.getScenarioId(), event.getOrganizationId(), event.getProjectId());

            // 2. Capture Outputs (Computed Snapshots)
            java.util.List<com.talentboozt.s_backend.domains.finance_planning.models.FinFinancialSnapshot> outputState = financialSnapshotRepository
                    .findByOrganizationIdAndProjectIdAndScenarioId(
                            event.getOrganizationId(), event.getProjectId(), event.getScenarioId());

            FinAiTrainingSnapshot snapshot = new FinAiTrainingSnapshot();
            snapshot.setOrganizationId(event.getOrganizationId());
            snapshot.setProjectId(event.getProjectId());
            snapshot.setScenarioId(event.getScenarioId());
            snapshot.setUserId(event.getUserId());
            snapshot.setChangedFields(event.getChangedFields());

            // Serialize to JSON
            snapshot.setInputSnapshot(objectMapper.writeValueAsString(Map.of(
                    "assumptions", inputState.getAssumptions(),
                    "sales", inputState.getSalesPlans(),
                    "pricing", inputState.getPricingModels(),
                    "budget", inputState.getBudgets())));

            snapshot.setOutputSnapshot(objectMapper.writeValueAsString(outputState));

            snapshot.setCreatedAt(Instant.now());

            aiTrainingSnapshotRepository.save(snapshot);
            log.info("Structured AI training snapshot saved successfully. Count: {}", outputState.size());
        } catch (Exception e) {
            log.error("Failed to capture AI training snapshot", e);
        }
    }
}
