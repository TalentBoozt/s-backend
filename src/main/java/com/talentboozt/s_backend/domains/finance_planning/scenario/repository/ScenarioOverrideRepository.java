package com.talentboozt.s_backend.domains.finance_planning.scenario.repository;

import com.talentboozt.s_backend.domains.finance_planning.scenario.models.ScenarioOverride;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScenarioOverrideRepository extends MongoRepository<ScenarioOverride, String> {
    List<ScenarioOverride> findByScenarioId(String scenarioId);
}
