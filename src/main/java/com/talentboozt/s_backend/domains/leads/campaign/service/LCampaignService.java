package com.talentboozt.s_backend.domains.leads.campaign.service;

import com.talentboozt.s_backend.domains.leads.campaign.model.LCampaign;
import com.talentboozt.s_backend.domains.leads.campaign.repository.LCampaignRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class LCampaignService {

    private final LCampaignRepository campaignRepository;

    public LCampaignService(LCampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }

    public List<LCampaign> getCampaignsByWorkspace(String workspaceId) {
        return campaignRepository.findByWorkspaceId(workspaceId);
    }

    public Optional<LCampaign> getCampaignById(String id, String workspaceId) {
        return campaignRepository.findById(id)
                .filter(c -> c.getWorkspaceId().equals(workspaceId));
    }

    public LCampaign createCampaign(LCampaign campaign, String workspaceId) {
        campaign.setWorkspaceId(workspaceId);
        campaign.setCreatedAt(Instant.now());
        campaign.setUpdatedAt(Instant.now());
        return campaignRepository.save(campaign);
    }

    public LCampaign updateCampaign(String id, String workspaceId, LCampaign update) {
        return getCampaignById(id, workspaceId)
                .map(existing -> {
                    existing.setName(update.getName());
                    existing.setStatus(update.getStatus());
                    existing.setTemplate(update.getTemplate());
                    existing.setUpdatedAt(Instant.now());
                    return campaignRepository.save(existing);
                }).orElseThrow(() -> new RuntimeException("Campaign not found"));
    }

    public void deleteCampaign(String id, String workspaceId) {
        getCampaignById(id, workspaceId).ifPresent(campaignRepository::delete);
    }
}
