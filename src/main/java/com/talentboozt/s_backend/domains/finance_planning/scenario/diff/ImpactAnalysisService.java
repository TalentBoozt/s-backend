package com.talentboozt.s_backend.domains.finance_planning.scenario.diff;

import com.talentboozt.s_backend.domains.finance_planning.models.FinFinancialSnapshot;
import com.talentboozt.s_backend.domains.finance_planning.scenario.resolver.ScenarioResolver.EffectiveProjectState;
import com.talentboozt.s_backend.domains.finance_planning.services.FinFinancialComputationService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImpactAnalysisService {
    private final FinFinancialComputationService computationService;

    public ImpactAnalysis analyzeImpact(EffectiveProjectState base, EffectiveProjectState scenario) {
        List<FinFinancialSnapshot> baseSnapshots = computationService.computeOnly(
            base.getAssumptions(), base.getSalesPlans(), base.getPricingModels(), base.getBudgets());
        
        List<FinFinancialSnapshot> scenarioSnapshots = computationService.computeOnly(
            scenario.getAssumptions(), scenario.getSalesPlans(), scenario.getPricingModels(), scenario.getBudgets());

        double baseRevenue = baseSnapshots.stream().mapToDouble(FinFinancialSnapshot::getRevenue).sum();
        double scenarioRevenue = scenarioSnapshots.stream().mapToDouble(FinFinancialSnapshot::getRevenue).sum();

        double baseCost = baseSnapshots.stream().mapToDouble(FinFinancialSnapshot::getCost).sum();
        double scenarioCost = scenarioSnapshots.stream().mapToDouble(FinFinancialSnapshot::getCost).sum();

        double baseProfit = baseSnapshots.stream().mapToDouble(FinFinancialSnapshot::getProfit).sum();
        double scenarioProfit = scenarioSnapshots.stream().mapToDouble(FinFinancialSnapshot::getProfit).sum();

        return ImpactAnalysis.builder()
            .revenueChange(calculatePercentChange(baseRevenue, scenarioRevenue))
            .costChange(calculatePercentChange(baseCost, scenarioCost))
            .profitChange(calculatePercentChange(baseProfit, scenarioProfit))
            .revenueImpact(scenarioRevenue - baseRevenue)
            .costImpact(scenarioCost - baseCost)
            .profitImpact(scenarioProfit - baseProfit)
            .build();
    }

    private double calculatePercentChange(double base, double scenario) {
        if (base == 0) return 0;
        return ((scenario - base) / base) * 100;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImpactAnalysis {
        private double revenueChange;
        private double costChange;
        private double profitChange;
        private double revenueImpact;
        private double costImpact;
        private double profitImpact;
    }
}
