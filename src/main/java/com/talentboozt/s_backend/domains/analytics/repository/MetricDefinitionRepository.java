package com.talentboozt.s_backend.domains.analytics.repository;

import com.talentboozt.s_backend.domains.analytics.models.MetricDefinition;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MetricDefinitionRepository extends MongoRepository<MetricDefinition, String> {
    List<MetricDefinition> findByOrganizationId(String organizationId);
    Optional<MetricDefinition> findByOrganizationIdAndKey(String organizationId, String key);
}
