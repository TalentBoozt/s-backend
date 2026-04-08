package com.talentboozt.s_backend.domains.leads.automation.controller;

import com.talentboozt.s_backend.domains.leads.automation.model.LLeadAutomation;
import com.talentboozt.s_backend.domains.leads.automation.repository.LLeadAutomationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/leads/automations")
public class LAutomationController {

    private final LLeadAutomationRepository automationRepository;
    private final com.talentboozt.s_backend.shared.security.utils.SecurityUtils securityUtils;

    public LAutomationController(LLeadAutomationRepository automationRepository, com.talentboozt.s_backend.shared.security.utils.SecurityUtils securityUtils) {
        this.automationRepository = automationRepository;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    public ResponseEntity<List<LLeadAutomation>> getAutomations() {
        String workspaceId = securityUtils.getCurrentWorkspaceId();
        return ResponseEntity.ok(automationRepository.findByWorkspaceId(workspaceId));
    }

    @PostMapping
    public ResponseEntity<LLeadAutomation> createAutomation(
            @RequestBody LLeadAutomation automation) {
        String workspaceId = securityUtils.getCurrentWorkspaceId();
        automation.setWorkspaceId(workspaceId);
        automation.setCreatedAt(Instant.now());
        automation.setUpdatedAt(Instant.now());
        return ResponseEntity.ok(automationRepository.save(automation));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LLeadAutomation> updateAutomation(
            @PathVariable String id,
            @RequestBody LLeadAutomation update) {
        
        String workspaceId = securityUtils.getCurrentWorkspaceId();
        return automationRepository.findById(id)
                .map(existing -> {
                    if (!existing.getWorkspaceId().equals(workspaceId)) {
                        throw new RuntimeException("Unauthorized");
                    }
                    existing.setName(update.getName());
                    existing.setActive(update.isActive());
                    existing.setTriggerType(update.getTriggerType());
                    existing.setTriggerScoreThreshold(update.getTriggerScoreThreshold());
                    existing.setTriggerKeywords(update.getTriggerKeywords());
                    existing.setActionType(update.getActionType());
                    existing.setActionWebhookUrl(update.getActionWebhookUrl());
                    existing.setRequiresHumanApproval(update.isRequiresHumanApproval());
                    existing.setUpdatedAt(Instant.now());
                    return ResponseEntity.ok(automationRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAutomation(
            @PathVariable String id) {
        
        String workspaceId = securityUtils.getCurrentWorkspaceId();
        automationRepository.findById(id).ifPresent(automation -> {
            if (automation.getWorkspaceId().equals(workspaceId)) {
                automationRepository.delete(automation);
            }
        });
        return ResponseEntity.noContent().build();
    }
}
