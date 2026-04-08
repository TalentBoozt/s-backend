package com.talentboozt.s_backend.domains.leads.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/leads")
public class LLeadsController {

    private final com.talentboozt.s_backend.domains.leads.repository.LRawSignalRepository rawSignalRepository;

    public LLeadsController(com.talentboozt.s_backend.domains.leads.repository.LRawSignalRepository rawSignalRepository) {
        this.rawSignalRepository = rawSignalRepository;
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "LeadOS module is up and running.";
    }

    @GetMapping("/signals")
    public java.util.List<com.talentboozt.s_backend.domains.leads.model.LRawSignal> getSignals(
            @org.springframework.web.bind.annotation.RequestHeader("X-Workspace-Id") String workspaceId) {
        // Simple fetch of all signals for the workspace, maybe sort by capturedAt desc
        java.util.List<com.talentboozt.s_backend.domains.leads.model.LRawSignal> signals = rawSignalRepository.findByWorkspaceId(workspaceId);
        signals.sort((a, b) -> b.getCapturedAt().compareTo(a.getCapturedAt()));
        return signals;
    }
}
