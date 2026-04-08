package com.talentboozt.s_backend.domains.leads.campaign.controller;

import com.talentboozt.s_backend.domains.leads.campaign.model.LCampaign;
import com.talentboozt.s_backend.domains.leads.campaign.service.LCampaignService;
import com.talentboozt.s_backend.shared.security.utils.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leads/campaigns")
public class LCampaignController {

    private final LCampaignService campaignService;
    private final SecurityUtils securityUtils;

    public LCampaignController(LCampaignService campaignService, SecurityUtils securityUtils) {
        this.campaignService = campaignService;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    public ResponseEntity<List<LCampaign>> getCampaigns() {
        String workspaceId = securityUtils.getCurrentWorkspaceId();
        return ResponseEntity.ok(campaignService.getCampaignsByWorkspace(workspaceId));
    }

    @PostMapping
    public ResponseEntity<LCampaign> createCampaign(@RequestBody LCampaign campaign) {
        String workspaceId = securityUtils.getCurrentWorkspaceId();
        return ResponseEntity.ok(campaignService.createCampaign(campaign, workspaceId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LCampaign> updateCampaign(@PathVariable String id, @RequestBody LCampaign campaign) {
        String workspaceId = securityUtils.getCurrentWorkspaceId();
        return ResponseEntity.ok(campaignService.updateCampaign(id, workspaceId, campaign));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCampaign(@PathVariable String id) {
        String workspaceId = securityUtils.getCurrentWorkspaceId();
        campaignService.deleteCampaign(id, workspaceId);
        return ResponseEntity.noContent().build();
    }
}
