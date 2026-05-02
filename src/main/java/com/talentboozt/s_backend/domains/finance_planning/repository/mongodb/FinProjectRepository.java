package com.talentboozt.s_backend.domains.finance_planning.repository.mongodb;

import com.talentboozt.s_backend.domains.finance_planning.models.FinProject;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FinProjectRepository extends MongoRepository<FinProject, String> {
    List<FinProject> findByOrganizationId(String organizationId);
    Optional<FinProject> findByOrganizationIdAndId(String organizationId, String projectId);
    long countByOrganizationId(String organizationId);
}
