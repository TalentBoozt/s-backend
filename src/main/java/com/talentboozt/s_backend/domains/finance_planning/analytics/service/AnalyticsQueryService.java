package com.talentboozt.s_backend.domains.finance_planning.analytics.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.talentboozt.s_backend.domains.finance_planning.analytics.models.AnalyticsData;
import com.talentboozt.s_backend.domains.finance_planning.analytics.models.MetricDefinition;
import com.talentboozt.s_backend.domains.finance_planning.analytics.repository.AnalyticsRepository;
import com.talentboozt.s_backend.domains.finance_planning.analytics.repository.MetricDefinitionRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsQueryService {
    private final AnalyticsRepository analyticsRepository;
    private final MetricDefinitionRepository metricDefinitionRepository;
    private final FormulaEngine formulaEngine;

    @Cacheable(value = "analytics", key = "#organizationId + ':' + #projectId + ':' + #scenarioId + ':' + #metric + ':' + #granularity")
    public Map<String, Object> getMetricData(String organizationId, String projectId, String scenarioId, String metric, String granularity) {
        // 1. Check if it's a precomputed standard metric
        List<AnalyticsData> results = analyticsRepository.findByOrganizationIdAndProjectIdAndScenarioIdAndMetricAndGranularity(
                organizationId, projectId, scenarioId, metric, granularity
        );

        if (!results.isEmpty()) {
            return formatResponse(metric, results);
        }

        // 2. Check if it's a custom metric
        Optional<MetricDefinition> definition = metricDefinitionRepository.findByOrganizationIdAndKey(organizationId, metric);
        if (definition.isPresent()) {
            return computeCustomMetric(organizationId, projectId, scenarioId, definition.get(), granularity);
        }

        // 3. Hybrid fallback: If not precomputed but we have the formula/logic, we could compute it here
        // For now, return empty or throw error
        return Map.<String, Object>of("metric", metric, "data", Collections.emptyList());
    }

    private Map<String, Object> computeCustomMetric(String orgId, String projId, String scenId, MetricDefinition definition, String granularity) {
        // Extract variables from formula using AST-based parser
        Set<String> requiredMetrics = formulaEngine.extractVariables(definition.getFormula());
        
        // Fetch data for all required base metrics
        Map<String, List<AnalyticsData>> baseData = new HashMap<>();
        for (String m : requiredMetrics) {
            baseData.put(m, analyticsRepository.findByOrganizationIdAndProjectIdAndScenarioIdAndMetricAndGranularity(orgId, projId, scenId, m, granularity));
        }

        // Align periods and calculate
        Set<String> periods = baseData.values().stream()
                .flatMap(List::stream)
                .map(AnalyticsData::getPeriod)
                .collect(Collectors.toSet());

        List<Map<String, Object>> computedData = periods.stream().sorted().map(period -> {
            Map<String, Double> variables = new HashMap<>();
            for (String m : requiredMetrics) {
                Double val = baseData.get(m).stream()
                        .filter(d -> d.getPeriod().equals(period))
                        .map(AnalyticsData::getValue)
                        .findFirst()
                        .orElse(0.0);
                variables.put(m, val);
            }
            
            Double value = formulaEngine.calculate(definition.getFormula(), variables);
            return Map.<String, Object>of("period", period, "value", value);
        }).collect(Collectors.toList());

        return Map.<String, Object>of("metric", definition.getKey(), "data", computedData);
    }

    private Map<String, Object> formatResponse(String metric, List<AnalyticsData> results) {
        List<Map<String, Object>> data = results.stream()
                .sorted(Comparator.comparing(AnalyticsData::getPeriod))
                .map(r -> Map.<String, Object>of("period", r.getPeriod(), "value", r.getValue()))
                .collect(Collectors.toList());
        
        return Map.<String, Object>of("metric", metric, "data", data);
    }
}
