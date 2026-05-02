package com.talentboozt.s_backend.domains.finance_planning.controllers;

import com.talentboozt.s_backend.domains.finance_planning.models.FinPricingModel;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.FinPricingModelRepository;
import com.talentboozt.s_backend.domains.finance_planning.security.annotations.RequiresFinPermission;
import com.talentboozt.s_backend.domains.finance_planning.security.rbac.FinPermission;
import com.talentboozt.s_backend.domains.finance_planning.services.FinFinancialComputationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/finance/pricing")
@RequiredArgsConstructor
public class FinPricingModelController {

    private final FinPricingModelRepository repository;
    private final FinFinancialComputationService computationService;

    @PostMapping
    @RequiresFinPermission(value = FinPermission.WRITE_PROJECT, orgIdSource = "header", projectIdSource = "header", projectIdKey = "X-Project-Id")
    public ResponseEntity<FinPricingModel> create(
            @RequestHeader("X-Organization-Id") String organizationId,
            @RequestBody FinPricingModel entity) {
        entity.setOrganizationId(organizationId);
        FinPricingModel saved = repository.save(entity);
        // Trigger recomputation async or via event
        computationService.recomputeFinancials(saved.getOrganizationId(), saved.getProjectId());
        return ResponseEntity.ok(saved);
    }
    
    @GetMapping
    @RequiresFinPermission(value = FinPermission.READ_PROJECT, orgIdSource = "header", projectIdSource = "header", projectIdKey = "X-Project-Id")
    public ResponseEntity<List<FinPricingModel>> getByProject(@RequestParam String projectId,
            @RequestHeader("X-Organization-Id") String organizationId) {
        return ResponseEntity.ok(repository.findByOrganizationIdAndProjectId(organizationId, projectId));
    }
}