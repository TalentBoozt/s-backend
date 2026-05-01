package com.talentboozt.s_backend.domains.analytics.repository;

import com.talentboozt.s_backend.domains.analytics.models.AnalyticsData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnalyticsRepository extends MongoRepository<AnalyticsData, String> {
    List<AnalyticsData> findByOrganizationIdAndProjectIdAndScenarioIdAndMetricAndGranularity(
            String organizationId, String projectId, String scenarioId, String metric, String granularity);

    Optional<AnalyticsData> findByOrganizationIdAndProjectIdAndScenarioIdAndMetricAndGranularityAndPeriod(
            String organizationId, String projectId, String scenarioId, String metric, String granularity, String period);

    void deleteByProjectIdAndScenarioId(String projectId, String scenarioId);
}
