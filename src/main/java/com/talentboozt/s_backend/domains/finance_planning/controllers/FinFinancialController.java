package com.talentboozt.s_backend.domains.finance_planning.controllers;

import com.talentboozt.s_backend.domains.finance_planning.models.FinFinancialSnapshot;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.FinFinancialSnapshotRepository;
import com.talentboozt.s_backend.domains.finance_planning.security.annotations.RequiresFinPermission;
import com.talentboozt.s_backend.domains.finance_planning.security.rbac.FinPermission;
import com.talentboozt.s_backend.domains.finance_planning.services.FinFinancialComputationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/finance/financials")
@RequiredArgsConstructor
public class FinFinancialController {

    private final FinFinancialSnapshotRepository repository;
    private final FinFinancialComputationService computationService;

    @GetMapping
    @RequiresFinPermission(value = FinPermission.READ_PROJECT, orgIdSource = "header", projectIdSource = "header", projectIdKey = "X-Project-Id")
    public ResponseEntity<?> getFinancials(
            @RequestHeader("X-Organization-Id") String organizationId, 
            @RequestParam String projectId,
            @RequestParam(required = false) String month) {
        if (month != null) {
            return ResponseEntity
                    .ok(repository.findByOrganizationIdAndProjectIdAndMonth(organizationId, projectId, month));
        }
        return ResponseEntity.ok(repository.findByOrganizationIdAndProjectId(organizationId, projectId));
    }

    @PostMapping("/recompute")
    @RequiresFinPermission(value = FinPermission.WRITE_PROJECT, orgIdSource = "header", projectIdSource = "header", projectIdKey = "X-Project-Id")
    public ResponseEntity<Void> recompute(
            @RequestHeader("X-Organization-Id") String organizationId,
            @RequestBody Map<String, String> payload) {
        computationService.recomputeFinancials(organizationId, payload.get("projectId"));
        return ResponseEntity.ok().build();
    }
}