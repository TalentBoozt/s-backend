package com.talentboozt.s_backend.domains.leads.crm.controller;

import com.talentboozt.s_backend.domains.leads.crm.dto.LConvertSignalRequest;
import com.talentboozt.s_backend.domains.leads.crm.dto.LLeadUpdateRequest;
import com.talentboozt.s_backend.domains.leads.crm.model.LLead;
import com.talentboozt.s_backend.domains.leads.crm.service.LLeadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leads")
public class LLeadController {

    private final LLeadService leadService;
    private final com.talentboozt.s_backend.shared.security.utils.SecurityUtils securityUtils;

    public LLeadController(LLeadService leadService, com.talentboozt.s_backend.shared.security.utils.SecurityUtils securityUtils) {
        this.leadService = leadService;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    public ResponseEntity<List<LLead>> getLeads(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Double minScore) {
        
        String workspaceId = securityUtils.getCurrentWorkspaceId();
        List<LLead> leads = leadService.getLeads(workspaceId, status, minScore);
        return ResponseEntity.ok(leads);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LLead> getLeadById(
            @PathVariable String id) {
        
        String workspaceId = securityUtils.getCurrentWorkspaceId();
        LLead lead = leadService.getLeadById(id, workspaceId);
        return ResponseEntity.ok(lead);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<LLead> updateLead(
            @PathVariable String id,
            @RequestBody LLeadUpdateRequest request) {
        
        String workspaceId = securityUtils.getCurrentWorkspaceId();
        LLead lead = leadService.updateLead(id, workspaceId, request.getStatus(), request.getNote());
        return ResponseEntity.ok(lead);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LLead> putUpdateLead(
            @PathVariable String id,
            @RequestBody LLeadUpdateRequest request) {
        
        String workspaceId = securityUtils.getCurrentWorkspaceId();
        LLead lead = leadService.updateLead(id, workspaceId, request.getStatus(), request.getNote());
        return ResponseEntity.ok(lead);
    }

    @PostMapping("/convert")
    public ResponseEntity<LLead> convertSignal(
            @RequestBody LConvertSignalRequest request) {
        
        String workspaceId = securityUtils.getCurrentWorkspaceId();
        LLead lead = leadService.convertFromSignal(request.getSignalId(), workspaceId);
        return ResponseEntity.ok(lead);
    }

    @PostMapping
    public ResponseEntity<LLead> createLead(
            @RequestBody LLead lead) {
        
        String workspaceId = securityUtils.getCurrentWorkspaceId();
        return ResponseEntity.ok(leadService.createLead(lead, workspaceId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLead(
            @PathVariable String id) {
        
        String workspaceId = securityUtils.getCurrentWorkspaceId();
        leadService.deleteLead(id, workspaceId);
        return ResponseEntity.noContent().build();
    }
}
