package com.talentboozt.s_backend.domains.finance_planning.controllers;

import com.talentboozt.s_backend.domains.finance_planning.models.FinScenario;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.FinScenarioRepository;
import com.talentboozt.s_backend.domains.finance_planning.security.annotations.RequiresFinPermission;
import com.talentboozt.s_backend.domains.finance_planning.security.rbac.FinPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/finance/scenarios/fin")
@RequiredArgsConstructor
public class FinScenarioController {

    private final FinScenarioRepository repository;

    @GetMapping
    @RequiresFinPermission(value = FinPermission.READ_PROJECT, orgIdSource = "header", projectIdSource = "header", projectIdKey = "X-Project-Id")
    public ResponseEntity<?> getScenarios(
            @RequestHeader("X-Organization-Id") String organizationId,
            @RequestParam String projectId) {
        return ResponseEntity.ok(repository.findByOrganizationIdAndProjectId(organizationId, projectId));
    }

    @PostMapping
    @RequiresFinPermission(value = FinPermission.MANAGE_SCENARIOS, orgIdSource = "header", projectIdSource = "header", projectIdKey = "X-Project-Id")
    public ResponseEntity<FinScenario> create(
            @RequestHeader("X-Organization-Id") String organizationId,
            @RequestBody FinScenario scenario) {
        scenario.setOrganizationId(organizationId);
        scenario.setCreatedAt(Instant.now());
        return ResponseEntity.ok(repository.save(scenario));
    }

    @PostMapping("/{id}/apply")
    @RequiresFinPermission(value = FinPermission.MANAGE_SCENARIOS, orgIdSource = "header", projectIdSource = "header", projectIdKey = "X-Project-Id")
    public ResponseEntity<Void> applyScenario(
            @RequestHeader("X-Organization-Id") String organizationId,
            @PathVariable String id) {
        // Logic to apply scenario overrides and trigger recompute
        return ResponseEntity.ok().build();
    }
}