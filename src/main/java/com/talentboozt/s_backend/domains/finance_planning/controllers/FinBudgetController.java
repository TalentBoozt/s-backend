package com.talentboozt.s_backend.domains.finance_planning.controllers;

import com.talentboozt.s_backend.domains.finance_planning.models.FinBudget;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.FinBudgetRepository;
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
    public ResponseEntity<FinBudget> create(@RequestBody FinBudget entity) {
        FinBudget saved = repository.save(entity);
        // Trigger recomputation async or via event
        computationService.recomputeFinancials(saved.getOrganizationId(), saved.getProjectId());
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<FinBudget>> getByProject(@RequestParam String projectId, @RequestParam String organizationId) {
        return ResponseEntity.ok(repository.findByOrganizationIdAndProjectId(organizationId, projectId));
    }
}