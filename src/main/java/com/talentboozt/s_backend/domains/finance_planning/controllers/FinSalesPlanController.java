package com.talentboozt.s_backend.domains.finance_planning.controllers;

import com.talentboozt.s_backend.domains.finance_planning.models.FinSalesPlan;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.FinSalesPlanRepository;
import com.talentboozt.s_backend.domains.finance_planning.security.annotations.RequiresFinPermission;
import com.talentboozt.s_backend.domains.finance_planning.security.rbac.FinPermission;
import com.talentboozt.s_backend.domains.finance_planning.services.FinFinancialComputationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/finance/sales")
@RequiredArgsConstructor
public class FinSalesPlanController {

    private final FinSalesPlanRepository repository;
    private final FinFinancialComputationService computationService;

    @PostMapping
    @RequiresFinPermission(value = FinPermission.WRITE_PROJECT, orgIdSource = "header", projectIdSource = "header", projectIdKey = "X-Project-Id")
    public ResponseEntity<FinSalesPlan> create(
            @RequestHeader("X-Organization-Id") String organizationId,
            @RequestBody FinSalesPlan entity) {
        entity.setOrganizationId(organizationId);
        FinSalesPlan saved = repository.save(entity);
        // Trigger recomputation async or via event
        computationService.recomputeFinancials(saved.getOrganizationId(), saved.getProjectId());
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    @RequiresFinPermission(value = FinPermission.READ_PROJECT, orgIdSource = "header")
    public ResponseEntity<List<FinSalesPlan>> getByProject(@RequestParam String projectId,
            @RequestHeader("X-Organization-Id") String organizationId) {
        return ResponseEntity.ok(repository.findByOrganizationIdAndProjectId(organizationId, projectId));
    }
}