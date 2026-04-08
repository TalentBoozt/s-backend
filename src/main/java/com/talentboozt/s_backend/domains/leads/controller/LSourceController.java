package com.talentboozt.s_backend.domains.leads.controller;

import com.talentboozt.s_backend.domains.leads.model.LLeadSource;
import com.talentboozt.s_backend.domains.leads.service.LLeadSourceService;
import com.talentboozt.s_backend.shared.security.utils.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leads/sources")
public class LSourceController {

    private final LLeadSourceService leadSourceService;
    private final SecurityUtils securityUtils;

    public LSourceController(LLeadSourceService leadSourceService, SecurityUtils securityUtils) {
        this.leadSourceService = leadSourceService;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    public ResponseEntity<List<LLeadSource>> getSources() {
        String workspaceId = securityUtils.getCurrentWorkspaceId();
        return ResponseEntity.ok(leadSourceService.getSourcesByWorkspace(workspaceId));
    }

    @PostMapping
    public ResponseEntity<LLeadSource> createSource(@RequestBody LLeadSource source) {
        String workspaceId = securityUtils.getCurrentWorkspaceId();
        source.setWorkspaceId(workspaceId);
        return ResponseEntity.ok(leadSourceService.createSource(source));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LLeadSource> updateSource(@PathVariable String id, @RequestBody LLeadSource source) {
        String workspaceId = securityUtils.getCurrentWorkspaceId();
        // Ensure the source belongs to the workspace
        return leadSourceService.getSourceById(id)
                .map(existing -> {
                    if (!existing.getWorkspaceId().equals(workspaceId)) {
                        return ResponseEntity.status(403).<LLeadSource>build();
                    }
                    return ResponseEntity.ok(leadSourceService.updateSource(id, source));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSource(@PathVariable String id) {
        String workspaceId = securityUtils.getCurrentWorkspaceId();
        leadSourceService.deleteSource(id, workspaceId);
        return ResponseEntity.noContent().build();
    }
}
