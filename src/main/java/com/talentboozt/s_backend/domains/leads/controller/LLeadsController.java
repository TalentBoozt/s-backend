package com.talentboozt.s_backend.domains.leads.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/leads")
public class LLeadsController {

    private final com.talentboozt.s_backend.domains.leads.repository.LRawSignalRepository rawSignalRepository;
    private final com.talentboozt.s_backend.shared.security.utils.SecurityUtils securityUtils;

    public LLeadsController(com.talentboozt.s_backend.domains.leads.repository.LRawSignalRepository rawSignalRepository,
                           com.talentboozt.s_backend.shared.security.utils.SecurityUtils securityUtils) {
        this.rawSignalRepository = rawSignalRepository;
        this.securityUtils = securityUtils;
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "LeadOS module is up and running.";
    }

    @GetMapping("/signals")
    public java.util.List<com.talentboozt.s_backend.domains.leads.model.LRawSignal> getSignals() {
        String workspaceId = securityUtils.getCurrentWorkspaceId();
        if (workspaceId == null) {
            throw new RuntimeException("No active workspace found for user");
        }
        // Simple fetch of all signals for the workspace, maybe sort by capturedAt desc
        java.util.List<com.talentboozt.s_backend.domains.leads.model.LRawSignal> signals = rawSignalRepository.findByWorkspaceId(workspaceId);
        signals.sort((a, b) -> b.getCapturedAt().compareTo(a.getCapturedAt()));
        return signals;
    }
}
