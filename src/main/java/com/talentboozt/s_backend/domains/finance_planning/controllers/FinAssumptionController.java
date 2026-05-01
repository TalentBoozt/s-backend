package com.talentboozt.s_backend.domains.finance_planning.controllers;

import com.talentboozt.s_backend.domains.finance_planning.models.FinAssumption;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.FinAssumptionRepository;
import com.talentboozt.s_backend.domains.finance_planning.security.annotations.RequiresFinPermission;
import com.talentboozt.s_backend.domains.finance_planning.security.rbac.FinPermission;
import com.talentboozt.s_backend.domains.finance_planning.services.FinFinancialComputationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/finance/assumptions")
@RequiredArgsConstructor
public class FinAssumptionController {

    private final FinAssumptionRepository repository;
    private final FinFinancialComputationService computationService;

    @PostMapping
    @RequiresFinPermission(value = FinPermission.WRITE_PROJECT, orgIdSource = "header")
    public ResponseEntity<FinAssumption> create(
            @RequestHeader("X-Organization-Id") String organizationId,
            @RequestBody FinAssumption entity) {
        entity.setOrganizationId(organizationId);
        FinAssumption saved = repository.save(entity);
        computationService.recomputeFinancials(saved.getOrganizationId(), saved.getProjectId());
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    @RequiresFinPermission(value = FinPermission.READ_PROJECT, orgIdSource = "header")
    public ResponseEntity<List<FinAssumption>> getByProject(
            @RequestParam String projectId, 
            @RequestHeader("X-Organization-Id") String organizationId) {
        return ResponseEntity.ok(repository.findByOrganizationIdAndProjectId(organizationId, projectId));
    }
}