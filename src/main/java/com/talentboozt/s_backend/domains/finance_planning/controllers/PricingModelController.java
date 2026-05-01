package com.talentboozt.s_backend.domains.finance_planning.controllers;

import com.talentboozt.s_backend.domains.finance_planning.models.PricingModel;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.PricingModelRepository;
import com.talentboozt.s_backend.domains.finance_planning.services.FinancialComputationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pricing")
@RequiredArgsConstructor
public class PricingModelController {

    private final PricingModelRepository repository;
    private final FinancialComputationService computationService;

    @PostMapping
    public ResponseEntity<PricingModel> create(@RequestBody PricingModel entity) {
        PricingModel saved = repository.save(entity);
        // Trigger recomputation async or via event
        computationService.recomputeFinancials(saved.getOrganizationId(), saved.getProjectId());
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<PricingModel>> getByProject(@RequestParam String projectId,
            @RequestParam String organizationId) {
        return ResponseEntity.ok(repository.findByOrganizationIdAndProjectId(organizationId, projectId));
    }
}