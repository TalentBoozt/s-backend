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

    /**
     * Recomputes the financial snapshots for a given project based on hardcoded
     * logic.
     * 
     * @param organizationId The organization ID
     * @param projectId      The project ID
     */
    @Transactional
    public void recomputeFinancials(String organizationId, String projectId) {
        log.info("Starting financial computation for org: {}, project: {}", organizationId, projectId);

        List<FinAssumption> assumptions = assumptionRepository.findByOrganizationIdAndProjectId(organizationId, projectId);
        List<FinSalesPlan> salesPlans = salesPlanRepository.findByOrganizationIdAndProjectId(organizationId, projectId);
        List<FinPricingModel> pricingModels = pricingModelRepository.findByOrganizationIdAndProjectId(organizationId,
                projectId);
        List<FinBudget> budgets = budgetRepository.findByOrganizationIdAndProjectId(organizationId, projectId);

        computeAndSave(organizationId, projectId, assumptions, salesPlans, pricingModels, budgets);
        log.info("Completed financial computation for org: {}, project: {}", organizationId, projectId);
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

    private void computeAndSave(String orgId, String projId, List<FinAssumption> assumptions, 
                               List<FinSalesPlan> salesPlans, List<FinPricingModel> pricingModels, List<FinBudget> budgets) {
        List<FinFinancialSnapshot> snapshots = computeOnly(assumptions, salesPlans, pricingModels, budgets);
        for (FinFinancialSnapshot s : snapshots) {
            saveSnapshot(orgId, projId, s.getMonth(), s.getRevenue(), s.getCost(), s.getProfit(), s.getBreakdown());
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
        double baseCost = 0.0;
        double marketingCost = 0.0;
        double storageCost = 0.0;
        double aiCost = 0.0;

        // Base user costs (from Pricing Model margins/COGS)
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

        // Variable/Fixed costs from Budget
        for (FinBudget budget : budgets) {
            if (budget.getMonthlyAllocations() != null) {
                double amount = budget.getMonthlyAllocations().getOrDefault(month, 0.0);
                if (budget.getCategory() != null) {
                    switch (budget.getCategory().toLowerCase()) {
                        case "marketing":
                            marketingCost += amount;
                            break;
                        case "storage":
                            storageCost += amount;
                            break;
                        case "ai":
                            aiCost += amount;
                            break;
                        default:
                            breakdown.put(budget.getCategory().toLowerCase(),
                                    breakdown.getOrDefault(budget.getCategory().toLowerCase(), 0.0) + amount);
                            break;
                    }
                }
            }
        }

        breakdown.put("marketingCost", marketingCost);
        breakdown.put("storageCost", storageCost);
        breakdown.put("aiCost", aiCost);

        return breakdown;
    }

    private void saveSnapshot(String organizationId, String projectId, String month, double revenue, double cost,
            double profit, Map<String, Double> breakdown) {
        FinFinancialSnapshot snapshot = financialSnapshotRepository
                .findByOrganizationIdAndProjectIdAndMonth(organizationId, projectId, month)
                .orElse(new FinFinancialSnapshot());

        snapshot.setOrganizationId(organizationId);
        snapshot.setProjectId(projectId);
        snapshot.setMonth(month);
        snapshot.setRevenue(revenue);
        snapshot.setCost(cost);
        snapshot.setProfit(profit);
        snapshot.setBreakdown(breakdown);
        snapshot.setComputedAt(Instant.now());

        financialSnapshotRepository.save(snapshot);
    }
}