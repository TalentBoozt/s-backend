package com.talentboozt.s_backend.domains.leads.repository;

import com.talentboozt.s_backend.domains.leads.model.LRawSignal;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LRawSignalRepository extends MongoRepository<LRawSignal, String> {
    List<LRawSignal> findByWorkspaceId(String workspaceId);
    List<LRawSignal> findBySourceId(String sourceId);
    boolean existsByPlatformId(String platformId);
}
