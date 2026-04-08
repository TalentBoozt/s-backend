package com.talentboozt.s_backend.domains.leads.crm.repository;

import com.talentboozt.s_backend.domains.leads.crm.model.LLead;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LLeadRepository extends MongoRepository<LLead, String> {
    
    @Query("{ 'workspaceId' : ?0, " +
            "  $and: [ " +
            "    { $or: [ { $where: '?1 == null' }, { 'status': ?1 } ] }, " +
            "    { $or: [ { $where: '?2 == null' }, { 'score': { $gte: ?2 } } ] } " +
            "  ] " +
            "}")
    List<LLead> findByFilters(String workspaceId, String status, Double minScore);

    List<LLead> findByWorkspaceId(String workspaceId);

    Optional<LLead> findBySourceSignalId(String sourceSignalId);
}
