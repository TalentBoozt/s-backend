package com.talentboozt.s_backend.domains.finance_planning.repository.mongodb;

import com.talentboozt.s_backend.domains.finance_planning.models.FinAssumption;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FinAssumptionRepository extends MongoRepository<FinAssumption, String> {
    List<FinAssumption> findByOrganizationIdAndProjectId(String organizationId, String projectId);
    Optional<FinAssumption> findByOrganizationIdAndProjectIdAndKey(String organizationId, String projectId, String key);
}