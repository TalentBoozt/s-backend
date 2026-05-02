package com.talentboozt.s_backend.domains.finance_planning.services;

import com.talentboozt.s_backend.domains.finance_planning.models.*;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinFinancialComputationService {

    private final FinAssumptionRepository assumptionRepository;
    private final FinSalesPlanRepository salesPlanRepository;
    private final FinPricingModelRepository pricingModelRepository;
    private final FinBudgetRepository budgetRepository;
    private final FinFinancialSnapshotRepository financialSnapshotRepository;
    private final org.springframework.context.ApplicationEventPublisher eventPublisher;
    private final com.talentboozt.s_backend.domains.finance_planning.scenario.resolver.ScenarioResolver scenarioResolver;
    private final com.talentboozt.s_backend.domains.finance_planning.analytics.service.FormulaEngine formulaEngine;
    private final io.micrometer.core.instrument.MeterRegistry meterRegistry;

    /**
     * Recomputes the financial snapshots for a given project and scenario incrementally.
     */
    @Transactional
    public void recomputeFinancials(String organizationId, String projectId, String scenarioId, String userId, 
                                   List<String> changedFields, List<String> affectedMonths) {
        long startTime = System.currentTimeMillis();
        String effectiveScenarioId = (scenarioId == null || "base".equalsIgnoreCase(scenarioId)) ? "base" : scenarioId;
        
        log.info("EVENT=COMPUTE_START organizationId={} projId={} scenarioId={} changes={}", 
                organizationId, projectId, effectiveScenarioId, changedFields);

        try {
            // 1. Resolve State
            com.talentboozt.s_backend.domains.finance_planning.scenario.resolver.ScenarioResolver.EffectiveProjectState state = 
                    scenarioResolver.resolveState(scenarioId, organizationId, projectId);

            // 2. Determine affected scope
            boolean globalChange = changedFields.stream().anyMatch(f -> 
                f.startsWith("assumption") || f.startsWith("pricing") || f.contains("formula"));
            
            List<String> finalAffectedMonths;
            if (globalChange || changedFields.isEmpty() || (affectedMonths != null && affectedMonths.isEmpty() && !changedFields.isEmpty())) {
                finalAffectedMonths = state.getSalesPlans().stream().map(FinSalesPlan::getMonth).toList();
            } else {
                finalAffectedMonths = affectedMonths;
            }

            // 3. Incremental Recompute
            computeAndSaveIncremental(organizationId, projectId, effectiveScenarioId, state, finalAffectedMonths);
            
            long duration = System.currentTimeMillis() - startTime;
            meterRegistry.timer("finance.computation.duration", "scenario", effectiveScenarioId)
                    .record(java.time.Duration.ofMillis(duration));
            meterRegistry.counter("finance.computation.success", "scenario", effectiveScenarioId).increment();

            log.info("EVENT=COMPUTE_SUCCESS durationMs={} monthsRecomputed={} scenarioId={}", 
                    duration, finalAffectedMonths.size(), effectiveScenarioId);

            eventPublisher.publishEvent(new com.talentboozt.s_backend.domains.finance_planning.events.FinancialsChangedEvent(
                    this, organizationId, projectId, effectiveScenarioId, userId, changedFields));
        } catch (Exception e) {
            meterRegistry.counter("finance.computation.failure", "scenario", effectiveScenarioId).increment();
            log.error("EVENT=COMPUTE_FAILURE scenarioId={} error={}", effectiveScenarioId, e.getMessage(), e);
            throw e;
        }
    }

    private void computeAndSaveIncremental(String organizationId, String projId, String scenarioId, 
                                          com.talentboozt.s_backend.domains.finance_planning.scenario.resolver.ScenarioResolver.EffectiveProjectState state,
                                          List<String> affectedMonths) {
        if (state.getSalesPlans().isEmpty()) return;

        // Prepare base variables once for all months to improve performance
        Map<String, Double> baseVariables = prepareBaseVariables(state.getAssumptions());

        for (FinSalesPlan plan : state.getSalesPlans()) {
            if (!affectedMonths.contains(plan.getMonth())) {
                continue;
            }

            String month = plan.getMonth();
            double totalRevenue = computeRevenue(plan, state.getPricingModels());
            Map<String, Double> costBreakdown = computeCosts(month, plan, state.getPricingModels(), state.getBudgets(), state.getAssumptions(), baseVariables);
            double totalCost = costBreakdown.values().stream().mapToDouble(Double::doubleValue).sum();
            double profit = totalRevenue - totalCost;

            saveSnapshot(organizationId, projId, scenarioId, month, totalRevenue, totalCost, profit, costBreakdown);
        }
    }

    @Transactional
    public void recomputeFinancials(String organizationId, String projectId) {
        recomputeFinancials(organizationId, projectId, "base", "system", new ArrayList<>(), null);
    }

    public List<FinFinancialSnapshot> computeOnly(List<FinAssumption> assumptions, List<FinSalesPlan> salesPlans, 
                                             List<FinPricingModel> pricingModels, List<FinBudget> budgets) {
        List<FinFinancialSnapshot> snapshots = new ArrayList<>();
        if (salesPlans.isEmpty() || pricingModels.isEmpty()) return snapshots;

        Map<String, Double> baseVariables = prepareBaseVariables(assumptions);

        for (FinSalesPlan plan : salesPlans) {
            String month = plan.getMonth();
            double totalRevenue = computeRevenue(plan, pricingModels);
            Map<String, Double> costBreakdown = computeCosts(month, plan, pricingModels, budgets, assumptions, baseVariables);
            double totalCost = costBreakdown.values().stream().mapToDouble(Double::doubleValue).sum();
            double profit = totalRevenue - totalCost;

            FinFinancialSnapshot snapshot = new FinFinancialSnapshot();
            snapshot.setMonth(month);
            snapshot.setRevenue(totalRevenue);
            snapshot.setCost(totalCost);
            snapshot.setProfit(profit);
            snapshot.setBreakdown(costBreakdown);
            snapshots.add(snapshot);
        }
        return snapshots;
    }

    private double computeRevenue(FinSalesPlan plan, List<FinPricingModel> pricingModels) {
        double revenue = 0.0;
        if (plan.getUserCounts() == null) return revenue;

        for (Map.Entry<String, Integer> entry : plan.getUserCounts().entrySet()) {
            String tier = entry.getKey();
            int count = entry.getValue();

            pricingModels.stream()
                    .filter(p -> p.getTier().equalsIgnoreCase(tier))
                    .findFirst()
                    .ifPresent(pricing -> {
                        // Use price from pricing model
                    });
            
            // Logic to actually use the price
            Optional<FinPricingModel> pricing = pricingModels.stream()
                    .filter(p -> p.getTier().equalsIgnoreCase(tier))
                    .findFirst();
            if (pricing.isPresent()) {
                revenue += pricing.get().getPrice() * count;
            }
        }
        return revenue;
    }

    private Map<String, Double> prepareBaseVariables(List<FinAssumption> assumptions) {
        Map<String, Double> variables = new HashMap<>();
        if (assumptions != null) {
            for (FinAssumption assumption : assumptions) {
                try {
                    variables.put(assumption.getKey(), Double.parseDouble(assumption.getValue()));
                } catch (NumberFormatException | NullPointerException e) {
                    log.warn("Assumption {} has non-numeric value: {}", assumption.getKey(), assumption.getValue());
                }
            }
        }
        return variables;
    }

    private Map<String, Double> computeCosts(String month, FinSalesPlan plan, List<FinPricingModel> pricingModels,
            List<FinBudget> budgets, List<FinAssumption> assumptions, Map<String, Double> baseVariables) {
        Map<String, Double> breakdown = new HashMap<>();
        
        // 1. Prepare variable context for Formula Engine (Clone base variables)
        Map<String, Double> variables = new HashMap<>(baseVariables);
        
        // Add Month-specific User counts to variables
        int totalUsers = 0;
        if (plan.getUserCounts() != null) {
            for (Map.Entry<String, Integer> entry : plan.getUserCounts().entrySet()) {
                variables.put(entry.getKey().toLowerCase() + "Users", (double) entry.getValue());
                totalUsers += entry.getValue();
            }
        }
        variables.put("totalUsers", (double) totalUsers);

        // 2. Base user costs (from Pricing Model margins/COGS)
        if (plan.getUserCounts() != null) {
            for (Map.Entry<String, Integer> entry : plan.getUserCounts().entrySet()) {
                String tier = entry.getKey();
                int count = entry.getValue();

                pricingModels.stream()
                        .filter(p -> p.getTier().equalsIgnoreCase(tier))
                        .findFirst()
                        .ifPresent(pricing -> breakdown.put("baseCost",
                                breakdown.getOrDefault("baseCost", 0.0) + (pricing.getCostPerUser() * count)));
            }
        }

        // 3. Variable/Fixed costs from Budget (Dynamic Categories)
        for (FinBudget budget : budgets) {
            if (budget.getCategory() == null) continue;
            
            String categoryKey = budget.getCategory().toLowerCase() + "Cost";
            double amount = 0.0;

            if (budget.getFormula() != null && !budget.getFormula().isEmpty()) {
                try {
                    amount = formulaEngine.calculate(budget.getFormula(), variables);
                } catch (Exception e) {
                    log.error("Failed to calculate formula for budget category {}: {}", budget.getCategory(), budget.getFormula(), e);
                    amount = budget.getMonthlyAllocations() != null ? budget.getMonthlyAllocations().getOrDefault(month, 0.0) : 0.0;
                }
            } else if (budget.getMonthlyAllocations() != null) {
                amount = budget.getMonthlyAllocations().getOrDefault(month, 0.0);
            }

            breakdown.put(categoryKey, breakdown.getOrDefault(categoryKey, 0.0) + amount);
        }

        return breakdown;
    }

    private void saveSnapshot(String organizationId, String projectId, String scenarioId, String month, double revenue, double cost,
            double profit, Map<String, Double> breakdown) {
        FinFinancialSnapshot snapshot = financialSnapshotRepository
                .findByOrganizationIdAndProjectIdAndScenarioIdAndMonth(organizationId, projectId, scenarioId, month)
                .orElse(new FinFinancialSnapshot());

        snapshot.setOrganizationId(organizationId);
        snapshot.setProjectId(projectId);
        snapshot.setScenarioId(scenarioId);
        snapshot.setMonth(month);
        snapshot.setRevenue(revenue);
        snapshot.setCost(cost);
        snapshot.setProfit(profit);
        snapshot.setBreakdown(breakdown);
        snapshot.setComputedAt(Instant.now());

        financialSnapshotRepository.save(snapshot);
    }
}