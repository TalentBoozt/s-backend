package com.talentboozt.s_backend.domains.finance_planning.repository.mongodb;

import com.talentboozt.s_backend.domains.finance_planning.models.Assumption;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssumptionRepository extends MongoRepository<Assumption, String> {
    List<Assumption> findByOrganizationIdAndProjectId(String organizationId, String projectId);
    Optional<Assumption> findByOrganizationIdAndProjectIdAndKey(String organizationId, String projectId, String key);
}