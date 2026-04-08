package com.talentboozt.s_backend.domains.leads.repository;

import com.talentboozt.s_backend.domains.leads.model.LLeadSource;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LLeadSourceRepository extends MongoRepository<LLeadSource, String> {
    List<LLeadSource> findByWorkspaceId(String workspaceId);
    List<LLeadSource> findByPlatformAndActive(String platform, boolean active);
}
