package com.talentboozt.s_backend.domains.leads.repository;

import com.talentboozt.s_backend.domains.leads.model.LTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface LTemplateRepository extends MongoRepository<LTemplate, String> {
    List<LTemplate> findByWorkspaceId(String workspaceId);
    List<LTemplate> findByWorkspaceIdOrderByUsageCountDesc(String workspaceId);
}
