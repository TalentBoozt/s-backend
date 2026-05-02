package com.talentboozt.s_backend.domains.finance_planning.analytics.service;

import com.talentboozt.s_backend.domains.finance_planning.analytics.models.AnalyticsData;
import com.talentboozt.s_backend.domains.finance_planning.analytics.repository.AnalyticsRepository;
import com.talentboozt.s_backend.domains.finance_planning.models.FinFinancialSnapshot;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsPrecomputationService {
    private final AnalyticsRepository analyticsRepository;
    private final com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.FinSalesPlanRepository salesPlanRepository;
    private final MongoTemplate mongoTemplate;
    private final FormulaEngine formulaEngine;

    public void precomputeAll(String organizationId, String projectId, String scenarioId) {
        // 1. Fetch raw data
        Query query = new Query(Criteria.where("projectId").is(projectId).and("organizationId").is(organizationId));
        List<FinFinancialSnapshot> snapshots = mongoTemplate.find(query, FinFinancialSnapshot.class);
        List<com.talentboozt.s_backend.domains.finance_planning.models.FinSalesPlan> salesPlans = salesPlanRepository.findByOrganizationIdAndProjectId(organizationId, projectId);

        if (snapshots.isEmpty()) return;

        // Sort snapshots by month for growth rate calculation
        snapshots.sort(Comparator.comparing(FinFinancialSnapshot::getMonth));

        // 2. Compute Monthly Aggregates
        for (int i = 0; i < snapshots.size(); i++) {
            FinFinancialSnapshot snapshot = snapshots.get(i);
            String month = snapshot.getMonth();
            
            saveAggregate(organizationId, projectId, scenarioId, "revenue", "MONTH", month, snapshot.getRevenue());
            saveAggregate(organizationId, projectId, scenarioId, "cost", "MONTH", month, snapshot.getCost());
            saveAggregate(organizationId, projectId, scenarioId, "profit", "MONTH", month, snapshot.getProfit());
            
            // Profit Margin
            double margin = snapshot.getRevenue() > 0 ? (snapshot.getProfit() / snapshot.getRevenue()) * 100 : 0;
            saveAggregate(organizationId, projectId, scenarioId, "profit_margin", "MONTH", month, margin);

            // Growth Rate (Revenue)
            if (i > 0) {
                double prevRevenue = snapshots.get(i - 1).getRevenue();
                double growth = prevRevenue > 0 ? ((snapshot.getRevenue() - prevRevenue) / prevRevenue) * 100 : 0;
                saveAggregate(organizationId, projectId, scenarioId, "growth_rate", "MONTH", month, growth);
            }

            // Cost per User
            Optional<com.talentboozt.s_backend.domains.finance_planning.models.FinSalesPlan> plan = salesPlans.stream()
                    .filter(p -> p.getMonth().equals(month))
                    .findFirst();
            if (plan.isPresent() && plan.get().getUserCounts() != null) {
                int totalUsers = plan.get().getUserCounts().values().stream().mapToInt(Integer::intValue).sum();
                double cpu = totalUsers > 0 ? snapshot.getCost() / totalUsers : 0;
                saveAggregate(organizationId, projectId, scenarioId, "cost_per_user", "MONTH", month, cpu);

                // Conversion Rate
                int payingUsers = plan.get().getUserCounts().getOrDefault("pro", 0) + plan.get().getUserCounts().getOrDefault("premium", 0);
                double convRate = totalUsers > 0 ? ((double) payingUsers / totalUsers) * 100 : 0;
                saveAggregate(organizationId, projectId, scenarioId, "conversion_rate", "MONTH", month, convRate);
            }
        }

        // 3. Compute Quarterly and Yearly Aggregates
        computeTimeAggregates(organizationId, projectId, scenarioId, snapshots);
    }

    private void computeTimeAggregates(String orgId, String projId, String scenId, List<FinFinancialSnapshot> snapshots) {
        // Group by Year
        Map<String, List<FinFinancialSnapshot>> byYear = snapshots.stream()
                .collect(Collectors.groupingBy(s -> s.getMonth().substring(0, 4)));

        byYear.forEach((year, yearlySnapshots) -> {
            double totalRevenue = yearlySnapshots.stream().mapToDouble(FinFinancialSnapshot::getRevenue).sum();
            double totalCost = yearlySnapshots.stream().mapToDouble(FinFinancialSnapshot::getCost).sum();
            double totalProfit = yearlySnapshots.stream().mapToDouble(FinFinancialSnapshot::getProfit).sum();

            saveAggregate(orgId, projId, scenId, "revenue", "YEAR", year, totalRevenue);
            saveAggregate(orgId, projId, scenId, "cost", "YEAR", year, totalCost);
            saveAggregate(orgId, projId, scenId, "profit", "YEAR", year, totalProfit);
            
            double margin = totalRevenue > 0 ? (totalProfit / totalRevenue) * 100 : 0;
            saveAggregate(orgId, projId, scenId, "profit_margin", "YEAR", year, margin);

            // Group by Quarter
            Map<Integer, List<FinFinancialSnapshot>> byQuarter = yearlySnapshots.stream()
                    .collect(Collectors.groupingBy(s -> (Integer.parseInt(s.getMonth().substring(5, 7)) - 1) / 3 + 1));

            byQuarter.forEach((quarter, quarterlySnapshots) -> {
                String period = year + "-Q" + quarter;
                double qRevenue = quarterlySnapshots.stream().mapToDouble(FinFinancialSnapshot::getRevenue).sum();
                double qCost = quarterlySnapshots.stream().mapToDouble(FinFinancialSnapshot::getCost).sum();
                double qProfit = quarterlySnapshots.stream().mapToDouble(FinFinancialSnapshot::getProfit).sum();

                saveAggregate(orgId, projId, scenId, "revenue", "QUARTER", period, qRevenue);
                saveAggregate(orgId, projId, scenId, "cost", "QUARTER", period, qCost);
                saveAggregate(orgId, projId, scenId, "profit", "QUARTER", period, qProfit);
                
                double qMargin = qRevenue > 0 ? (qProfit / qRevenue) * 100 : 0;
                saveAggregate(orgId, projId, scenId, "profit_margin", "QUARTER", period, qMargin);
            });
        });
    }

    private void saveAggregate(String orgId, String projId, String scenId, String metric, String granularity, String period, Double value) {
        AnalyticsData data = analyticsRepository.findByOrganizationIdAndProjectIdAndScenarioIdAndMetricAndGranularityAndPeriod(
                orgId, projId, scenId, metric, granularity, period
        ).orElse(AnalyticsData.builder()
                .organizationId(orgId)
                .projectId(projId)
                .scenarioId(scenId)
                .metric(metric)
                .granularity(granularity)
                .period(period)
                .build());

        data.setValue(value);
        data.setComputedAt(Instant.now());
        analyticsRepository.save(data);
    }
}
