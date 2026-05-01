package com.talentboozt.s_backend.domains.finance_planning.controllers;

import com.talentboozt.s_backend.domains.finance_planning.models.FinScenario;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.FinScenarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/scenarios")
@RequiredArgsConstructor
public class FinScenarioController {

    private final FinScenarioRepository repository;

    @PostMapping
    public ResponseEntity<FinScenario> create(@RequestBody FinScenario scenario) {
        scenario.setCreatedAt(Instant.now());
        return ResponseEntity.ok(repository.save(scenario));
    }

    @PostMapping("/{id}/apply")
    public ResponseEntity<Void> applyScenario(@PathVariable String id) {
        // Logic to apply scenario overrides and trigger recompute
        return ResponseEntity.ok().build();
    }
}