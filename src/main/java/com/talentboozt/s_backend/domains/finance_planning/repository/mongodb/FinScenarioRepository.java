package com.talentboozt.s_backend.domains.finance_planning.repository.mongodb;

import com.talentboozt.s_backend.domains.finance_planning.models.FinScenario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FinScenarioRepository extends MongoRepository<FinScenario, String> {
    List<FinScenario> findByOrganizationIdAndProjectId(String organizationId, String projectId);
}