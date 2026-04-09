package com.talentboozt.s_backend.domains.leads.controller;

import com.talentboozt.s_backend.domains.leads.campaign.model.LCampaign;
import com.talentboozt.s_backend.domains.leads.campaign.repository.LCampaignRepository;
import com.talentboozt.s_backend.domains.leads.model.LLeadCandidate;
import com.talentboozt.s_backend.domains.leads.repository.LLeadCandidateRepository;
import com.talentboozt.s_backend.shared.security.utils.SecurityUtils;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/leads/search")
public class LSearchController {
    
    private final LLeadCandidateRepository leadRepository;
    private final LCampaignRepository campaignRepository;
    private final SecurityUtils securityUtils;

    public LSearchController(LLeadCandidateRepository leadRepository, 
                            LCampaignRepository campaignRepository, 
                            SecurityUtils securityUtils) {
        this.leadRepository = leadRepository;
        this.campaignRepository = campaignRepository;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    public Map<String, Object> globalSearch(@RequestParam String q) {
        String wsId = securityUtils.getCurrentWorkspaceId();
        if (wsId == null) throw new RuntimeException("No workspace found");
        
        String query = q.toLowerCase();
        
        // Search Leads
        List<LLeadCandidate> leads = leadRepository.findByWorkspaceId(wsId).stream()
            .filter(l -> (l.getName() != null && l.getName().toLowerCase().contains(query)) || 
                         (l.getTags() != null && l.getTags().stream().anyMatch(t -> t.toLowerCase().contains(query))))
            .limit(5)
            .collect(Collectors.toList());
            
        // Search Campaigns
        List<LCampaign> campaigns = campaignRepository.findByWorkspaceId(wsId).stream()
            .filter(c -> c.getName() != null && c.getName().toLowerCase().contains(query))
            .limit(5)
            .collect(Collectors.toList());
            
        Map<String, Object> results = new HashMap<>();
        results.put("leads", leads);
        results.put("campaigns", campaigns);
        results.put("query", q);
        
        return results;
    }
}
