package com.talentboozt.s_backend.domains.finance_planning.controllers;

import com.talentboozt.s_backend.domains.finance_planning.models.FinBudget;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.FinBudgetRepository;
import com.talentboozt.s_backend.domains.finance_planning.security.annotations.RequiresFinPermission;
import com.talentboozt.s_backend.domains.finance_planning.security.rbac.FinPermission;
import com.talentboozt.s_backend.domains.finance_planning.services.FinFinancialComputationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/budget")
@RequiredArgsConstructor
public class FinBudgetController {

    private final FinBudgetRepository repository;
    private final FinFinancialComputationService computationService;

    @PostMapping
    @RequiresFinPermission(value = FinPermission.WRITE_PROJECT, orgIdSource = "header")
    public ResponseEntity<FinBudget> create(
            @RequestHeader("X-Organization-Id") String organizationId,
            @RequestBody FinBudget entity) {
        entity.setOrganizationId(organizationId);
        FinBudget saved = repository.save(entity);
        // Trigger recomputation async or via event
        computationService.recomputeFinancials(saved.getOrganizationId(), saved.getProjectId());
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    @RequiresFinPermission(value = FinPermission.READ_PROJECT, orgIdSource = "param", orgIdKey = "organizationId")
    public ResponseEntity<List<FinBudget>> getByProject(
            @RequestParam String projectId, 
            @RequestParam String organizationId) {
        return ResponseEntity.ok(repository.findByOrganizationIdAndProjectId(organizationId, projectId));
    }
}