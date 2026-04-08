package com.talentboozt.s_backend.domains.leads.automation.repository;

import com.talentboozt.s_backend.domains.leads.automation.model.LLeadAutomation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LLeadAutomationRepository extends MongoRepository<LLeadAutomation, String> {
    List<LLeadAutomation> findByWorkspaceIdAndActiveTrue(String workspaceId);
    List<LLeadAutomation> findByWorkspaceId(String workspaceId);
}
