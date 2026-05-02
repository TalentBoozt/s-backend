package com.talentboozt.s_backend.domains.finance_planning.repository.mongodb;

import com.talentboozt.s_backend.domains.finance_planning.models.FinProjectMember;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FinProjectMemberRepository extends MongoRepository<FinProjectMember, String> {
    List<FinProjectMember> findByProjectId(String projectId);
    Optional<FinProjectMember> findByProjectIdAndUserId(String projectId, String userId);
}
