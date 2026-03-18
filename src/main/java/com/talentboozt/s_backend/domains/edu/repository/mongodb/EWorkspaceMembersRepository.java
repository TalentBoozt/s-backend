package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.talentboozt.s_backend.domains.edu.model.EWorkspaceMembers;
import java.util.List;
import java.util.Optional;

@Repository
public interface EWorkspaceMembersRepository extends MongoRepository<EWorkspaceMembers, String> {
    List<EWorkspaceMembers> findByWorkspaceId(String workspaceId);
    List<EWorkspaceMembers> findByUserId(String userId);
    Optional<EWorkspaceMembers> findByWorkspaceIdAndUserId(String workspaceId, String userId);
}
