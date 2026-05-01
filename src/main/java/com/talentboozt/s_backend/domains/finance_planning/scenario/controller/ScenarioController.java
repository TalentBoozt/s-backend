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
import com.talentboozt.s_backend.domains.finance_planning.security.annotations.RequiresFinPermission;
import com.talentboozt.s_backend.domains.finance_planning.security.rbac.FinPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

import com.talentboozt.s_backend.shared.dto.ApiResponse;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/v1/finance/scenarios")
@RequiredArgsConstructor
public class ScenarioController {
    private final ScenarioRepository scenarioRepository;
    private final ScenarioOverrideRepository overrideRepository;
    private final ScenarioResolver scenarioResolver;
    private final ScenarioDiffEngine diffEngine;
    private final ImpactAnalysisService impactService;
    private final ScenarioMergeService mergeService;

    @PostMapping
    @RequiresFinPermission(value = FinPermission.MANAGE_SCENARIOS, orgIdSource = "header")
    public ResponseEntity<ApiResponse<Scenario>> createScenario(
            @RequestHeader("X-Organization-Id") String organizationId,
            @RequestBody Scenario scenario) {
        scenario.setOrganizationId(organizationId);
        scenario.setCreatedAt(Instant.now());
        return ResponseEntity.ok(ApiResponse.success(scenarioRepository.save(scenario)));
    }

    @GetMapping
    @RequiresFinPermission(value = FinPermission.READ_PROJECT, orgIdSource = "header")
    public ResponseEntity<ApiResponse<List<Scenario>>> listScenarios(
            @RequestHeader("X-Organization-Id") String organizationId,
            @RequestParam String projectId) {
        return ResponseEntity.ok(ApiResponse.success(scenarioRepository.findByProjectId(projectId)));
    }

    @PostMapping("/{id}/override")
    @RequiresFinPermission(value = FinPermission.MANAGE_SCENARIOS, orgIdSource = "header")
    public ResponseEntity<ApiResponse<ScenarioOverride>> addOverride(
            @RequestHeader("X-Organization-Id") String organizationId,
            @PathVariable String id, 
            @RequestBody ScenarioOverride override) {
        override.setScenarioId(id);
        override.setCreatedAt(Instant.now());
        return ResponseEntity.ok(ApiResponse.success(overrideRepository.save(override)));
    }

    @GetMapping("/{id}/state")
    @RequiresFinPermission(value = FinPermission.READ_PROJECT, orgIdSource = "param", orgIdKey = "organizationId")
    public ResponseEntity<ApiResponse<EffectiveProjectState>> getResolvedState(
            @PathVariable String id, 
            @RequestParam String organizationId, 
            @RequestParam String projectId) {
        return ResponseEntity.ok(ApiResponse.success(scenarioResolver.resolveState(id, organizationId, projectId)));
    }

    @GetMapping("/{id}/diff")
    @RequiresFinPermission(value = FinPermission.READ_PROJECT, orgIdSource = "param", orgIdKey = "organizationId")
    public ResponseEntity<ApiResponse<List<DiffResult>>> getDiff(
            @PathVariable String id, 
            @RequestParam String organizationId, 
            @RequestParam String projectId, 
            @RequestParam(required = false) String compareWith) {
        EffectiveProjectState current = scenarioResolver.resolveState(id, organizationId, projectId);
        EffectiveProjectState other;
        if (compareWith == null || "base".equals(compareWith)) {
            other = scenarioResolver.resolveState(null, organizationId, projectId);
        } else {
            other = scenarioResolver.resolveState(compareWith, organizationId, projectId);
        }
        return ResponseEntity.ok(ApiResponse.success(diffEngine.compare(other, current)));
    }

    @GetMapping("/{id}/impact")
    @RequiresFinPermission(value = FinPermission.VIEW_ANALYTICS, orgIdSource = "param", orgIdKey = "organizationId")
    public ResponseEntity<ApiResponse<ImpactAnalysisService.ImpactAnalysis>> getImpact(
            @PathVariable String id, 
            @RequestParam String organizationId, 
            @RequestParam String projectId, 
            @RequestParam(required = false) String compareWith) {
        EffectiveProjectState current = scenarioResolver.resolveState(id, organizationId, projectId);
        EffectiveProjectState other;
        if (compareWith == null || "base".equals(compareWith)) {
            other = scenarioResolver.resolveState(null, organizationId, projectId);
        } else {
            other = scenarioResolver.resolveState(compareWith, organizationId, projectId);
        }
        return ResponseEntity.ok(ApiResponse.success(impactService.analyzeImpact(other, current)));
    }

    @PostMapping("/{id}/merge")
    @RequiresFinPermission(value = FinPermission.MANAGE_SCENARIOS, orgIdSource = "header")
    public ResponseEntity<ApiResponse<Void>> merge(
            @RequestHeader("X-Organization-Id") String organizationId,
            @PathVariable String id, 
            @RequestParam(required = false) String targetId) {
        if (targetId == null || "main".equals(targetId)) {
            mergeService.mergeIntoMain(id);
        } else {
            mergeService.mergeScenarios(id, targetId);
        }
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
