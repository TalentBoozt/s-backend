package com.talentboozt.s_backend.domains.finance_planning.analytics.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.talentboozt.s_backend.domains.finance_planning.analytics.models.MetricDefinition;

import java.util.List;
import java.util.Optional;

@Repository
public interface MetricDefinitionRepository extends MongoRepository<MetricDefinition, String> {
    List<MetricDefinition> findByOrganizationId(String organizationId);
    Optional<MetricDefinition> findByOrganizationIdAndKey(String organizationId, String key);
}
