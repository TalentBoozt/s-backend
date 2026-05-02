package com.talentboozt.s_backend.domains.finance_planning.scenario.resolver;

import com.talentboozt.s_backend.domains.finance_planning.models.*;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.*;
import com.talentboozt.s_backend.domains.finance_planning.scenario.models.Scenario;
import com.talentboozt.s_backend.domains.finance_planning.scenario.models.ScenarioOverride;
import com.talentboozt.s_backend.domains.finance_planning.scenario.repository.ScenarioOverrideRepository;
import com.talentboozt.s_backend.domains.finance_planning.scenario.repository.ScenarioRepository;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioResolver {
    private final ScenarioRepository scenarioRepository;
    private final ScenarioOverrideRepository overrideRepository;

    private final FinAssumptionRepository assumptionRepository;
    private final FinSalesPlanRepository salesPlanRepository;
    private final FinBudgetRepository budgetRepository;
    private final FinPricingModelRepository pricingModelRepository;

    public EffectiveProjectState resolveState(String scenarioId, String organizationId, String projectId) {
        if (scenarioId == null || "base".equalsIgnoreCase(scenarioId)) {
            return loadBaseState(organizationId, projectId);
        }

        Scenario scenario = scenarioRepository.findById(scenarioId)
                .orElseThrow(() -> new RuntimeException("Scenario not found"));

        // 1. Collect all scenarios in the hierarchy (from root to current)
        List<Scenario> hierarchy = new ArrayList<>();
        Scenario current = scenario;
        while (current != null) {
            hierarchy.add(0, current);
            if (current.getParentScenarioId() != null) {
                current = scenarioRepository.findById(current.getParentScenarioId()).orElse(null);
            } else {
                current = null;
            }
        }

        // 2. Load Base Data
        EffectiveProjectState state = loadBaseState(scenario.getOrganizationId(), scenario.getProjectId());

        // 3. Apply Overrides for each scenario in the hierarchy
        for (Scenario s : hierarchy) {
            List<ScenarioOverride> overrides = overrideRepository.findByScenarioId(s.getId());
            applyOverrides(state, overrides);
        }

        return state;
    }

    private EffectiveProjectState loadBaseState(String organizationId, String projectId) {
        EffectiveProjectState state = new EffectiveProjectState();
        
        state.setAssumptions(assumptionRepository.findByOrganizationIdAndProjectId(organizationId, projectId)
                .stream().map(this::copyAssumption).toList());
        
        state.setSalesPlans(salesPlanRepository.findByOrganizationIdAndProjectId(organizationId, projectId)
                .stream().map(this::copySalesPlan).toList());
        
        state.setBudgets(budgetRepository.findByOrganizationIdAndProjectId(organizationId, projectId)
                .stream().map(this::copyBudget).toList());
        
        state.setPricingModels(pricingModelRepository.findByOrganizationIdAndProjectId(organizationId, projectId));
        return state;
    }

    private void applyOverrides(EffectiveProjectState state, List<ScenarioOverride> overrides) {
        for (ScenarioOverride override : overrides) {
            String path = override.getPath();
            if (path.startsWith("sales.")) {
                String tier = path.substring(6);
                state.getSalesPlans().stream()
                        .filter(p -> p.getMonth().equals(override.getMonth()))
                        .findFirst()
                        .ifPresent(p -> {
                            Map<String, Integer> counts = new HashMap<>(p.getUserCounts());
                            counts.put(tier, ((Number) override.getValue()).intValue());
                            p.setUserCounts(counts);
                        });
            } else if (path.startsWith("budget.")) {
                String category = path.substring(7);
                state.getBudgets().stream()
                        .filter(b -> b.getCategory().equalsIgnoreCase(category))
                        .findFirst()
                        .ifPresent(b -> {
                            Map<String, Double> allocations = new HashMap<>(b.getMonthlyAllocations());
                            allocations.put(override.getMonth(), ((Number) override.getValue()).doubleValue());
                            b.setMonthlyAllocations(allocations);
                        });
            } else if (path.startsWith("assumption.")) {
                String key = path.substring(11);
                state.getAssumptions().stream()
                        .filter(a -> a.getKey().equalsIgnoreCase(key))
                        .findFirst()
                        .ifPresent(a -> a.setValue(override.getValue().toString()));
            }
        }
    }

    private FinAssumption copyAssumption(FinAssumption original) {
        FinAssumption copy = new FinAssumption();
        copy.setId(original.getId());
        copy.setOrganizationId(original.getOrganizationId());
        copy.setProjectId(original.getProjectId());
        copy.setKey(original.getKey());
        copy.setValue(original.getValue());
        copy.setUnit(original.getUnit());
        copy.setCategory(original.getCategory());
        copy.setVersion(original.getVersion());
        copy.setCreatedAt(original.getCreatedAt());
        return copy;
    }

    private FinSalesPlan copySalesPlan(FinSalesPlan original) {
        FinSalesPlan copy = new FinSalesPlan();
        copy.setId(original.getId());
        copy.setOrganizationId(original.getOrganizationId());
        copy.setProjectId(original.getProjectId());
        copy.setMonth(original.getMonth());
        copy.setUserCounts(original.getUserCounts() != null ? new HashMap<>(original.getUserCounts()) : new HashMap<>());
        copy.setGrowthRate(original.getGrowthRate());
        copy.setVersion(original.getVersion());
        copy.setCreatedAt(original.getCreatedAt());
        return copy;
    }

    private FinBudget copyBudget(FinBudget original) {
        FinBudget copy = new FinBudget();
        copy.setId(original.getId());
        copy.setOrganizationId(original.getOrganizationId());
        copy.setProjectId(original.getProjectId());
        copy.setCategory(original.getCategory());
        copy.setMonthlyAllocations(original.getMonthlyAllocations() != null ? new HashMap<>(original.getMonthlyAllocations()) : new HashMap<>());
        copy.setFormula(original.getFormula());
        copy.setVersion(original.getVersion());
        copy.setCreatedAt(original.getCreatedAt());
        return copy;
    }

    @Data
    public static class EffectiveProjectState {
        private List<FinAssumption> assumptions;
        private List<FinSalesPlan> salesPlans;
        private List<FinBudget> budgets;
        private List<FinPricingModel> pricingModels;
    }
}
