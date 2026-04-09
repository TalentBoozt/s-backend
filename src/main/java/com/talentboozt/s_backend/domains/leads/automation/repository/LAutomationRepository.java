package com.talentboozt.s_backend.domains.leads.automation.repository;

import com.talentboozt.s_backend.domains.leads.automation.model.LAutomation;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface LAutomationRepository extends MongoRepository<LAutomation, String> {
    List<LAutomation> findByWorkspaceId(String workspaceId);
}
