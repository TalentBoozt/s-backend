package com.talentboozt.s_backend.domains.finance_planning.controllers;

import com.talentboozt.s_backend.domains.finance_planning.models.FinAssumption;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.FinAssumptionRepository;
import com.talentboozt.s_backend.domains.finance_planning.services.FinFinancialComputationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assumptions")
@RequiredArgsConstructor
public class FinAssumptionController {

    private final FinAssumptionRepository repository;
    private final FinFinancialComputationService computationService;

    @PostMapping
    public ResponseEntity<FinAssumption> create(@RequestBody FinAssumption entity) {
        FinAssumption saved = repository.save(entity);
        // Trigger recomputation async or via event
        computationService.recomputeFinancials(saved.getOrganizationId(), saved.getProjectId());
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<FinAssumption>> getByProject(@RequestParam String projectId, @RequestParam String organizationId) {
        return ResponseEntity.ok(repository.findByOrganizationIdAndProjectId(organizationId, projectId));
    }
}