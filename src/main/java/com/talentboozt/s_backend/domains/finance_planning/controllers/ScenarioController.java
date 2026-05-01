package com.talentboozt.s_backend.domains.finance_planning.controllers;

import com.talentboozt.s_backend.domains.finance_planning.models.Scenario;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.ScenarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/scenarios")
@RequiredArgsConstructor
public class ScenarioController {

    private final ScenarioRepository repository;

    @PostMapping
    public ResponseEntity<Scenario> create(@RequestBody Scenario scenario) {
        scenario.setCreatedAt(Instant.now());
        return ResponseEntity.ok(repository.save(scenario));
    }

    @PostMapping("/{id}/apply")
    public ResponseEntity<Void> applyScenario(@PathVariable String id) {
        // Logic to apply scenario overrides and trigger recompute
        return ResponseEntity.ok().build();
    }
}