package com.talentboozt.s_backend.domains.leads.analytics.controller;

import com.talentboozt.s_backend.domains.leads.analytics.service.LAnalyticsService;
import com.talentboozt.s_backend.domains.leads.crm.service.LLeadService;
import com.talentboozt.s_backend.domains.leads.crm.model.LLead;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/leads/analytics")
public class LAnalyticsController {

    private final LAnalyticsService analyticsService;
    private final LLeadService leadService;

    public LAnalyticsController(LAnalyticsService analyticsService, LLeadService leadService) {
        this.analyticsService = analyticsService;
        this.leadService = leadService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAnalytics(@RequestHeader("X-Workspace-Id") String workspaceId) {
        return ResponseEntity.ok(analyticsService.getWorkspaceAnalytics(workspaceId));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportLeadsCsv(@RequestHeader("X-Workspace-Id") String workspaceId) {
        List<LLead> leads = leadService.getLeads(workspaceId, null, null);
        
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("ID,Name,Platform,Status,Score,CreatedAt\n");
        for (LLead lead : leads) {
            csvBuilder.append(lead.getId()).append(",")
                    .append(lead.getName() != null ? lead.getName().replace(",", " ") : "").append(",")
                    .append(lead.getPlatform()).append(",")
                    .append(lead.getStatus()).append(",")
                    .append(lead.getScore() != null ? lead.getScore() : 0).append(",")
                    .append(lead.getCreatedAt()).append("\n");
        }

        byte[] csvBytes = csvBuilder.toString().getBytes();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "leads_export.csv");

        return ResponseEntity.ok()
                .headers(headers)
                .body(csvBytes);
    }
}
