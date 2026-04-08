package com.talentboozt.s_backend.domains.leads.repository;

import com.talentboozt.s_backend.domains.leads.model.LLeadCandidate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LLeadCandidateRepository extends MongoRepository<LLeadCandidate, String> {
    List<LLeadCandidate> findByWorkspaceId(String workspaceId);
}
