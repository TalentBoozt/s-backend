package com.talentboozt.s_backend.domains.finance_planning.controllers;

import com.talentboozt.s_backend.domains.finance_planning.models.FinPricingModel;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.FinPricingModelRepository;
import com.talentboozt.s_backend.domains.finance_planning.services.FinFinancialComputationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pricing")
@RequiredArgsConstructor
public class FinPricingModelController {

    private final FinPricingModelRepository repository;
    private final FinFinancialComputationService computationService;

    @PostMapping
    public ResponseEntity<FinPricingModel> create(@RequestBody FinPricingModel entity) {
        FinPricingModel saved = repository.save(entity);
        // Trigger recomputation async or via event
        computationService.recomputeFinancials(saved.getOrganizationId(), saved.getProjectId());
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<FinPricingModel>> getByProject(@RequestParam String projectId,
            @RequestParam String organizationId) {
        return ResponseEntity.ok(repository.findByOrganizationIdAndProjectId(organizationId, projectId));
    }
}