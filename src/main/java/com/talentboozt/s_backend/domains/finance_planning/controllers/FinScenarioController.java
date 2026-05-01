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
@RequestMapping("/api/v1/finance/scenarios")
@RequiredArgsConstructor
public class FinScenarioController {

    private final FinScenarioRepository repository;

    @PostMapping
    @RequiresFinPermission(value = FinPermission.MANAGE_SCENARIOS, orgIdSource = "header")
    public ResponseEntity<FinScenario> create(
            @RequestHeader("X-Organization-Id") String organizationId,
            @RequestBody FinScenario scenario) {
        scenario.setOrganizationId(organizationId);
        scenario.setCreatedAt(Instant.now());
        return ResponseEntity.ok(repository.save(scenario));
    }

    @PostMapping("/{id}/apply")
    @RequiresFinPermission(value = FinPermission.MANAGE_SCENARIOS, orgIdSource = "header")
    public ResponseEntity<Void> applyScenario(
            @RequestHeader("X-Organization-Id") String organizationId,
            @PathVariable String id) {
        // Logic to apply scenario overrides and trigger recompute
        return ResponseEntity.ok().build();
    }
}