package com.talentboozt.s_backend.domains.finance_planning.repository.mongodb;

import com.talentboozt.s_backend.domains.finance_planning.models.FinWorkspace;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FinWorkspaceRepository extends MongoRepository<FinWorkspace, String> {
    List<FinWorkspace> findByMemberIdsContaining(String userId);
    Optional<FinWorkspace> findBySlug(String slug);
}
