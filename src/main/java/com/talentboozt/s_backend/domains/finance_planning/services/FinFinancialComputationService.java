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
    private final com.talentboozt.s_backend.domains.analytics.service.FormulaEngine formulaEngine;

    /**
     * Recomputes the financial snapshots for a given project and scenario.
     * 
     * @param organizationId The organization ID
     * @param projectId      The project ID
     * @param scenarioId     The scenario ID (null or "base" for base state)
     * @param userId         The ID of the user who triggered the change
     * @param changedFields  List of fields that were updated
     */
    @Transactional
    public void recomputeFinancials(String organizationId, String projectId, String scenarioId, String userId, List<String> changedFields) {
        String effectiveScenarioId = (scenarioId == null || "base".equalsIgnoreCase(scenarioId)) ? "base" : scenarioId;
        log.info("Starting financial computation for org: {}, project: {}, scenario: {}, user: {}", 
                organizationId, projectId, effectiveScenarioId, userId);

        com.talentboozt.s_backend.domains.finance_planning.scenario.resolver.ScenarioResolver.EffectiveProjectState state = 
                scenarioResolver.resolveState(scenarioId, organizationId, projectId);

        computeAndSave(organizationId, projectId, effectiveScenarioId, 
                state.getAssumptions(), state.getSalesPlans(), state.getPricingModels(), state.getBudgets());
        
        // Notify analytics engine with full context
        eventPublisher.publishEvent(new com.talentboozt.s_backend.domains.finance_planning.events.FinancialsChangedEvent(
                this, organizationId, projectId, effectiveScenarioId, userId, changedFields));
        
        log.info("Completed financial computation for org: {}, project: {}, scenario: {}", 
                organizationId, projectId, effectiveScenarioId);
    }

    /**
     * Legacy support for base state recomputation
     */
    @Transactional
    public void recomputeFinancials(String organizationId, String projectId) {
        recomputeFinancials(organizationId, projectId, "base", "system", new ArrayList<>());
    }

    public List<FinFinancialSnapshot> computeOnly(List<FinAssumption> assumptions, List<FinSalesPlan> salesPlans, 
                                             List<FinPricingModel> pricingModels, List<FinBudget> budgets) {
        List<FinFinancialSnapshot> snapshots = new ArrayList<>();
        if (salesPlans.isEmpty() || pricingModels.isEmpty()) return snapshots;

        for (FinSalesPlan plan : salesPlans) {
            String month = plan.getMonth();
            double totalRevenue = computeRevenue(plan, pricingModels);
            Map<String, Double> costBreakdown = computeCosts(month, plan, pricingModels, budgets, assumptions);
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

    private void computeAndSave(String orgId, String projId, String scenarioId, List<FinAssumption> assumptions, 
                               List<FinSalesPlan> salesPlans, List<FinPricingModel> pricingModels, List<FinBudget> budgets) {
        List<FinFinancialSnapshot> snapshots = computeOnly(assumptions, salesPlans, pricingModels, budgets);
        for (FinFinancialSnapshot s : snapshots) {
            saveSnapshot(orgId, projId, scenarioId, s.getMonth(), s.getRevenue(), s.getCost(), s.getProfit(), s.getBreakdown());
        }
    }

    private double computeRevenue(FinSalesPlan plan, List<FinPricingModel> pricingModels) {
        double revenue = 0.0;
        if (plan.getUserCounts() == null)
            return revenue;

        for (Map.Entry<String, Integer> entry : plan.getUserCounts().entrySet()) {
            String tier = entry.getKey();
            int count = entry.getValue();

            Optional<FinPricingModel> pricing = pricingModels.stream()
                    .filter(p -> p.getTier().equalsIgnoreCase(tier))
                    .findFirst();

            if (pricing.isPresent()) {
                revenue += pricing.get().getPrice() * count;
            }
        }
        return revenue;
    }

    private Map<String, Double> computeCosts(String month, FinSalesPlan plan, List<FinPricingModel> pricingModels,
            List<FinBudget> budgets, List<FinAssumption> assumptions) {
        Map<String, Double> breakdown = new HashMap<>();
        
        // 1. Prepare variable context for Formula Engine
        Map<String, Double> variables = new HashMap<>();
        
        // Add Assumptions to variables
        if (assumptions != null) {
            for (FinAssumption assumption : assumptions) {
                try {
                    variables.put(assumption.getKey(), Double.parseDouble(assumption.getValue()));
                } catch (NumberFormatException | NullPointerException e) {
                    log.warn("Assumption {} has non-numeric value: {}", assumption.getKey(), assumption.getValue());
                }
            }
        }
        
        // Add User counts to variables
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
                // Use Formula Engine
                try {
                    amount = formulaEngine.calculate(budget.getFormula(), variables);
                } catch (Exception e) {
                    log.error("Failed to calculate formula for budget category {}: {}", budget.getCategory(), budget.getFormula(), e);
                    // Fallback to monthly allocation if formula fails
                    amount = budget.getMonthlyAllocations() != null ? budget.getMonthlyAllocations().getOrDefault(month, 0.0) : 0.0;
                }
            } else if (budget.getMonthlyAllocations() != null) {
                // Use fixed monthly allocation
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