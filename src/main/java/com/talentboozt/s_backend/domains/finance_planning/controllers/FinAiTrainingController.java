package com.talentboozt.s_backend.domains.finance_planning.controllers;

import com.talentboozt.s_backend.domains.finance_planning.models.FinAiTrainingSnapshot;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.FinAiTrainingSnapshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ai-training")
@RequiredArgsConstructor
public class FinAiTrainingController {

    private final FinAiTrainingSnapshotRepository repository;

    @GetMapping("/export")
    public ResponseEntity<List<FinAiTrainingSnapshot>> export(@RequestParam String organizationId,
            @RequestParam String projectId) {
        return ResponseEntity.ok(repository.findByOrganizationIdAndProjectId(organizationId, projectId));
    }

    @PostMapping("/tag")
    public ResponseEntity<FinAiTrainingSnapshot> tagSnapshot(@RequestParam String id, @RequestBody List<String> tags) {
        FinAiTrainingSnapshot snapshot = repository.findById(id).orElseThrow();
        snapshot.setTags(tags);
        return ResponseEntity.ok(repository.save(snapshot));
    }
}