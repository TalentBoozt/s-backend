package com.talentboozt.s_backend.domains.finance_planning.controllers;

import com.talentboozt.s_backend.domains.finance_planning.models.FinAiTrainingSnapshot;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.FinAiTrainingSnapshotRepository;
import com.talentboozt.s_backend.domains.finance_planning.security.annotations.RequiresFinPermission;
import com.talentboozt.s_backend.domains.finance_planning.security.rbac.FinPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/finance/ai-training")
@RequiredArgsConstructor
public class FinAiTrainingController {

    private final FinAiTrainingSnapshotRepository repository;

    @GetMapping("/export")
    @RequiresFinPermission(value = FinPermission.READ_PROJECT, orgIdSource = "header")
    public ResponseEntity<List<FinAiTrainingSnapshot>> export(
            @RequestHeader("X-Organization-Id") String organizationId,
            @RequestParam String projectId) {
        return ResponseEntity.ok(repository.findByOrganizationIdAndProjectId(organizationId, projectId));
    }

    @PostMapping("/tag")
    @RequiresFinPermission(value = FinPermission.WRITE_PROJECT, orgIdSource = "header")
    public ResponseEntity<FinAiTrainingSnapshot> tagSnapshot(
            @RequestHeader("X-Organization-Id") String organizationId,
            @RequestParam String id, 
            @RequestBody List<String> tags) {
        FinAiTrainingSnapshot snapshot = repository.findById(id).orElseThrow();
        snapshot.setTags(tags);
        return ResponseEntity.ok(repository.save(snapshot));
    }
}