package com.talentboozt.s_backend.domains.finance_planning.scenario.merge;

import com.talentboozt.s_backend.domains.finance_planning.collaboration.models.CollaborationOperation;
import com.talentboozt.s_backend.domains.finance_planning.collaboration.models.OperationType;
import com.talentboozt.s_backend.domains.finance_planning.collaboration.service.OperationProcessor;
import com.talentboozt.s_backend.domains.finance_planning.scenario.models.Scenario;
import com.talentboozt.s_backend.domains.finance_planning.scenario.models.ScenarioOverride;
import com.talentboozt.s_backend.domains.finance_planning.scenario.repository.ScenarioOverrideRepository;
import com.talentboozt.s_backend.domains.finance_planning.scenario.repository.ScenarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioMergeService {
    private final ScenarioRepository scenarioRepository;
    private final ScenarioOverrideRepository overrideRepository;
    private final OperationProcessor operationProcessor;

    /**
     * Merges a scenario into the main project state.
     */
    @Transactional
    public void mergeIntoMain(String scenarioId) {
        Scenario scenario = scenarioRepository.findById(scenarioId)
            .orElseThrow(() -> new RuntimeException("Scenario not found"));

        List<ScenarioOverride> overrides = overrideRepository.findByScenarioId(scenarioId);

        for (ScenarioOverride override : overrides) {
            // Convert override to collaboration operation to reuse logic
            CollaborationOperation op = CollaborationOperation.builder()
                .type(OperationType.UPDATE_CELL) // Simplified for now
                .organizationId(scenario.getOrganizationId())
                .projectId(scenario.getProjectId())
                .path(override.getPath())
                .month(override.getMonth())
                .value(override.getValue())
                .userId(scenario.getCreatedBy())
                .timestamp(System.currentTimeMillis())
                .version(System.currentTimeMillis())
                .build();
            
            operationProcessor.processOperation(op);
        }
        
        // Optionally mark scenario as merged or delete it
        log.info("Merged scenario {} into main project state", scenarioId);
    }

    /**
     * Merges Scenario A into Scenario B.
     */
    @Transactional
    public void mergeScenarios(String sourceId, String targetId) {
        List<ScenarioOverride> sourceOverrides = overrideRepository.findByScenarioId(sourceId);
        
        for (ScenarioOverride source : sourceOverrides) {
            ScenarioOverride target = ScenarioOverride.builder()
                .scenarioId(targetId)
                .path(source.getPath())
                .month(source.getMonth())
                .value(source.getValue())
                .type(source.getType())
                .createdBy(source.getCreatedBy())
                .createdAt(source.getCreatedAt())
                .build();
            
            overrideRepository.save(target);
        }
        
        log.info("Merged scenario {} into scenario {}", sourceId, targetId);
    }
}
