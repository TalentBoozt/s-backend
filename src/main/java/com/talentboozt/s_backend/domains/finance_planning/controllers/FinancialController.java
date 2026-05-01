package com.talentboozt.s_backend.domains.finance_planning.controllers;

import com.talentboozt.s_backend.domains.finance_planning.models.FinancialSnapshot;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.FinancialSnapshotRepository;
import com.talentboozt.s_backend.domains.finance_planning.services.FinancialComputationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/financials")
@RequiredArgsConstructor
public class FinancialController {

    private final FinancialSnapshotRepository repository;
    private final FinancialComputationService computationService;

    @GetMapping
    public ResponseEntity<?> getFinancials(@RequestParam String organizationId, @RequestParam String projectId,
            @RequestParam(required = false) String month) {
        if (month != null) {
            return ResponseEntity
                    .ok(repository.findByOrganizationIdAndProjectIdAndMonth(organizationId, projectId, month));
        }
        return ResponseEntity.ok(repository.findByOrganizationIdAndProjectId(organizationId, projectId));
    }

    @PostMapping("/recompute")
    public ResponseEntity<Void> recompute(@RequestBody Map<String, String> payload) {
        computationService.recomputeFinancials(payload.get("organizationId"), payload.get("projectId"));
        return ResponseEntity.ok().build();
    }
}