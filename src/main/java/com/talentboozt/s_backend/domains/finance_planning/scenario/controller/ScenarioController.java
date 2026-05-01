package com.talentboozt.s_backend.domains.finance_planning.scenario.controller;

import com.talentboozt.s_backend.domains.finance_planning.scenario.diff.DiffResult;
import com.talentboozt.s_backend.domains.finance_planning.scenario.diff.ImpactAnalysisService;
import com.talentboozt.s_backend.domains.finance_planning.scenario.diff.ScenarioDiffEngine;
import com.talentboozt.s_backend.domains.finance_planning.scenario.merge.ScenarioMergeService;
import com.talentboozt.s_backend.domains.finance_planning.scenario.models.Scenario;
import com.talentboozt.s_backend.domains.finance_planning.scenario.models.ScenarioOverride;
import com.talentboozt.s_backend.domains.finance_planning.scenario.repository.ScenarioOverrideRepository;
import com.talentboozt.s_backend.domains.finance_planning.scenario.repository.ScenarioRepository;
import com.talentboozt.s_backend.domains.finance_planning.scenario.resolver.ScenarioResolver;
import com.talentboozt.s_backend.domains.finance_planning.scenario.resolver.ScenarioResolver.EffectiveProjectState;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/scenarios")
@RequiredArgsConstructor
public class ScenarioController {
    private final ScenarioRepository scenarioRepository;
    private final ScenarioOverrideRepository overrideRepository;
    private final ScenarioResolver scenarioResolver;
    private final ScenarioDiffEngine diffEngine;
    private final ImpactAnalysisService impactService;
    private final ScenarioMergeService mergeService;

    @PostMapping
    public Scenario createScenario(@RequestBody Scenario scenario) {
        scenario.setCreatedAt(Instant.now());
        return scenarioRepository.save(scenario);
    }

    @GetMapping
    public List<Scenario> listScenarios(@RequestParam String projectId) {
        return scenarioRepository.findByProjectId(projectId);
    }

    @PostMapping("/{id}/override")
    public ScenarioOverride addOverride(@PathVariable String id, @RequestBody ScenarioOverride override) {
        override.setScenarioId(id);
        override.setCreatedAt(Instant.now());
        return overrideRepository.save(override);
    }

    @GetMapping("/{id}/state")
    public EffectiveProjectState getResolvedState(@PathVariable String id, @RequestParam String organizationId, @RequestParam String projectId) {
        return scenarioResolver.resolveState(id, organizationId, projectId);
    }

    @GetMapping("/{id}/diff")
    public List<DiffResult> getDiff(@PathVariable String id, @RequestParam String organizationId, @RequestParam String projectId, @RequestParam(required = false) String compareWith) {
        EffectiveProjectState current = scenarioResolver.resolveState(id, organizationId, projectId);
        EffectiveProjectState other;
        if (compareWith == null || "base".equals(compareWith)) {
            other = scenarioResolver.resolveState(null, organizationId, projectId);
        } else {
            other = scenarioResolver.resolveState(compareWith, organizationId, projectId);
        }
        return diffEngine.compare(other, current);
    }

    @GetMapping("/{id}/impact")
    public ImpactAnalysisService.ImpactAnalysis getImpact(@PathVariable String id, @RequestParam String organizationId, @RequestParam String projectId, @RequestParam(required = false) String compareWith) {
        EffectiveProjectState current = scenarioResolver.resolveState(id, organizationId, projectId);
        EffectiveProjectState other;
        if (compareWith == null || "base".equals(compareWith)) {
            other = scenarioResolver.resolveState(null, organizationId, projectId);
        } else {
            other = scenarioResolver.resolveState(compareWith, organizationId, projectId);
        }
        return impactService.analyzeImpact(other, current);
    }

    @PostMapping("/{id}/merge")
    public void merge(@PathVariable String id, @RequestParam(required = false) String targetId) {
        if (targetId == null || "main".equals(targetId)) {
            mergeService.mergeIntoMain(id);
        } else {
            mergeService.mergeScenarios(id, targetId);
        }
    }
}
