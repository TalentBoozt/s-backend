package com.talentboozt.s_backend.domains.workspace.repository;

import com.talentboozt.s_backend.domains.workspace.model.WorkspaceModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkspaceRepository extends MongoRepository<WorkspaceModel, String> {
    List<WorkspaceModel> findByOwnerId(String ownerId);
    List<WorkspaceModel> findByMemberIdsContaining(String memberId);
}
