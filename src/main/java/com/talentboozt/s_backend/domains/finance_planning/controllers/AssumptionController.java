package com.talentboozt.s_backend.domains.finance_planning.controllers;

import com.talentboozt.s_backend.domains.finance_planning.models.Assumption;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.AssumptionRepository;
import com.talentboozt.s_backend.domains.finance_planning.services.FinancialComputationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assumptions")
@RequiredArgsConstructor
public class AssumptionController {

    private final AssumptionRepository repository;
    private final FinancialComputationService computationService;

    @PostMapping
    public ResponseEntity<Assumption> create(@RequestBody Assumption entity) {
        Assumption saved = repository.save(entity);
        // Trigger recomputation async or via event
        computationService.recomputeFinancials(saved.getOrganizationId(), saved.getProjectId());
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<Assumption>> getByProject(@RequestParam String projectId, @RequestParam String organizationId) {
        return ResponseEntity.ok(repository.findByOrganizationIdAndProjectId(organizationId, projectId));
    }
}