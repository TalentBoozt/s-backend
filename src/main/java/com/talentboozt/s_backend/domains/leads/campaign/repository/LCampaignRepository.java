package com.talentboozt.s_backend.domains.leads.campaign.repository;

import com.talentboozt.s_backend.domains.leads.campaign.model.LCampaign;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LCampaignRepository extends MongoRepository<LCampaign, String> {
    List<LCampaign> findByWorkspaceId(String workspaceId);
}
