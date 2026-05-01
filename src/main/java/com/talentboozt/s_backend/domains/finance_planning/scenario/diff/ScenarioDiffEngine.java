package com.talentboozt.s_backend.domains.finance_planning.scenario.diff;

import com.talentboozt.s_backend.domains.finance_planning.models.FinAssumption;
import com.talentboozt.s_backend.domains.finance_planning.models.FinSalesPlan;
import com.talentboozt.s_backend.domains.finance_planning.scenario.resolver.ScenarioResolver.EffectiveProjectState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioDiffEngine {

    public List<DiffResult> compare(EffectiveProjectState base, EffectiveProjectState scenario) {
        List<DiffResult> results = new ArrayList<>();

        // 1. Compare Assumptions
        for (FinAssumption bA : base.getAssumptions()) {
            scenario.getAssumptions().stream()
                .filter(sA -> sA.getKey().equals(bA.getKey()))
                .findFirst()
                .ifPresent(sA -> {
                    if (!Objects.equals(bA.getValue(), sA.getValue())) {
                        results.add(DiffResult.builder()
                            .field("assumption." + bA.getKey())
                            .baseValue(bA.getValue())
                            .scenarioValue(sA.getValue())
                            .impact(calculateImpact(bA.getValue(), sA.getValue()))
                            .build());
                    }
                });
        }

        // 2. Compare Sales Plans
        for (FinSalesPlan bS : base.getSalesPlans()) {
            scenario.getSalesPlans().stream()
                .filter(sS -> sS.getMonth().equals(bS.getMonth()))
                .findFirst()
                .ifPresent(sS -> {
                    for (Map.Entry<String, Integer> entry : bS.getUserCounts().entrySet()) {
                        String tier = entry.getKey();
                        Integer bVal = entry.getValue();
                        Integer sVal = sS.getUserCounts().get(tier);
                        if (!Objects.equals(bVal, sVal)) {
                            results.add(DiffResult.builder()
                                .field("sales." + tier)
                                .month(bS.getMonth())
                                .baseValue(bVal)
                                .scenarioValue(sVal)
                                .impact(calculateImpact(bVal, sVal))
                                .build());
                        }
                    }
                });
        }

        return results;
    }

    private String calculateImpact(Object base, Object scenario) {
        try {
            double b = Double.parseDouble(base.toString());
            double s = Double.parseDouble(scenario.toString());
            if (b == 0) return s > 0 ? "+100%" : "0%";
            double diff = ((s - b) / b) * 100;
            return String.format("%s%.1f%%", diff > 0 ? "+" : "", diff);
        } catch (Exception e) {
            return "Changed";
        }
    }
}
