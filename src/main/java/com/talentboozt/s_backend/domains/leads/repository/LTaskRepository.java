package com.talentboozt.s_backend.domains.leads.repository;

import com.talentboozt.s_backend.domains.leads.model.LTask;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface LTaskRepository extends MongoRepository<LTask, String> {
    List<LTask> findByWorkspaceIdOrderByCreatedAtDesc(String workspaceId);
    List<LTask> findByWorkspaceIdAndStatusIn(String workspaceId, List<String> statuses);
}
