package com.talentboozt.s_backend.domains.finance_planning.scenario.repository;

import com.talentboozt.s_backend.domains.finance_planning.scenario.models.Scenario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScenarioRepository extends MongoRepository<Scenario, String> {
    List<Scenario> findByProjectId(String projectId);
    List<Scenario> findByOrganizationIdAndProjectId(String organizationId, String projectId);
}
