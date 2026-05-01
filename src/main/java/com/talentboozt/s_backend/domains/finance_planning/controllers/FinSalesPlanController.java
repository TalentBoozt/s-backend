package com.talentboozt.s_backend.domains.finance_planning.controllers;

import com.talentboozt.s_backend.domains.finance_planning.models.FinSalesPlan;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.FinSalesPlanRepository;
import com.talentboozt.s_backend.domains.finance_planning.services.FinFinancialComputationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
public class FinSalesPlanController {

    private final FinSalesPlanRepository repository;
    private final FinFinancialComputationService computationService;

    @PostMapping
    public ResponseEntity<FinSalesPlan> create(@RequestBody FinSalesPlan entity) {
        FinSalesPlan saved = repository.save(entity);
        // Trigger recomputation async or via event
        computationService.recomputeFinancials(saved.getOrganizationId(), saved.getProjectId());
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<FinSalesPlan>> getByProject(@RequestParam String projectId,
            @RequestParam String organizationId) {
        return ResponseEntity.ok(repository.findByOrganizationIdAndProjectId(organizationId, projectId));
    }
}