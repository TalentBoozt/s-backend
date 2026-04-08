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

    public LLeadController(LLeadService leadService) {
        this.leadService = leadService;
    }

    @GetMapping
    public ResponseEntity<List<LLead>> getLeads(
            @RequestHeader("X-Workspace-Id") String workspaceId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Double minScore) {
        
        List<LLead> leads = leadService.getLeads(workspaceId, status, minScore);
        return ResponseEntity.ok(leads);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LLead> getLeadById(
            @PathVariable String id,
            @RequestHeader("X-Workspace-Id") String workspaceId) {
        
        LLead lead = leadService.getLeadById(id, workspaceId);
        return ResponseEntity.ok(lead);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<LLead> updateLead(
            @PathVariable String id,
            @RequestHeader("X-Workspace-Id") String workspaceId,
            @RequestBody LLeadUpdateRequest request) {
        
        LLead lead = leadService.updateLead(id, workspaceId, request.getStatus(), request.getNote());
        return ResponseEntity.ok(lead);
    }

    @PostMapping("/convert")
    public ResponseEntity<LLead> convertSignal(
            @RequestHeader("X-Workspace-Id") String workspaceId,
            @RequestBody LConvertSignalRequest request) {
        
        LLead lead = leadService.convertFromSignal(request.getSignalId(), workspaceId);
        return ResponseEntity.ok(lead);
    }
}
