package com.talentboozt.s_backend.domains.leads.repository;

import com.talentboozt.s_backend.domains.leads.model.LLeadWorkspace;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LLeadWorkspaceRepository extends MongoRepository<LLeadWorkspace, String> {
    List<LLeadWorkspace> findByOwnerId(String ownerId);
}
