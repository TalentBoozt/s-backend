package com.talentboozt.s_backend.domains.finance_planning.controllers;

import com.talentboozt.s_backend.domains.finance_planning.models.SalesPlan;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.SalesPlanRepository;
import com.talentboozt.s_backend.domains.finance_planning.services.FinancialComputationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SalesPlanController {

    private final SalesPlanRepository repository;
    private final FinancialComputationService computationService;

    @PostMapping
    public ResponseEntity<SalesPlan> create(@RequestBody SalesPlan entity) {
        SalesPlan saved = repository.save(entity);
        // Trigger recomputation async or via event
        computationService.recomputeFinancials(saved.getOrganizationId(), saved.getProjectId());
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<SalesPlan>> getByProject(@RequestParam String projectId,
            @RequestParam String organizationId) {
        return ResponseEntity.ok(repository.findByOrganizationIdAndProjectId(organizationId, projectId));
    }
}