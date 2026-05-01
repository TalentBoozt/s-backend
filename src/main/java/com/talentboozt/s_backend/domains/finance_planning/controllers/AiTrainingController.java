package com.talentboozt.s_backend.domains.finance_planning.controllers;

import com.talentboozt.s_backend.domains.finance_planning.models.AiTrainingSnapshot;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.AiTrainingSnapshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ai-training")
@RequiredArgsConstructor
public class AiTrainingController {

    private final AiTrainingSnapshotRepository repository;

    @GetMapping("/export")
    public ResponseEntity<List<AiTrainingSnapshot>> export(@RequestParam String organizationId,
            @RequestParam String projectId) {
        return ResponseEntity.ok(repository.findByOrganizationIdAndProjectId(organizationId, projectId));
    }

    @PostMapping("/tag")
    public ResponseEntity<AiTrainingSnapshot> tagSnapshot(@RequestParam String id, @RequestBody List<String> tags) {
        AiTrainingSnapshot snapshot = repository.findById(id).orElseThrow();
        snapshot.setTags(tags);
        return ResponseEntity.ok(repository.save(snapshot));
    }
}