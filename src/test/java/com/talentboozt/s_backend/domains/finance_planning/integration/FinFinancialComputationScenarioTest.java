package com.talentboozt.s_backend.domains.finance_planning.integration;

import com.talentboozt.s_backend.domains.finance_planning.models.*;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.*;
import com.talentboozt.s_backend.domains.finance_planning.scenario.models.Scenario;
import com.talentboozt.s_backend.domains.finance_planning.scenario.models.ScenarioOverride;
import com.talentboozt.s_backend.domains.finance_planning.scenario.repository.ScenarioOverrideRepository;
import com.talentboozt.s_backend.domains.finance_planning.scenario.repository.ScenarioRepository;
import com.talentboozt.s_backend.domains.finance_planning.services.FinFinancialComputationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class FinFinancialComputationScenarioTest {

    @Autowired
    private FinFinancialComputationService computationService;

    @Autowired
    private FinSalesPlanRepository salesPlanRepository;

    @Autowired
    private FinPricingModelRepository pricingModelRepository;

    @Autowired
    private FinFinancialSnapshotRepository snapshotRepository;

    @Autowired
    private ScenarioRepository scenarioRepository;

    @Autowired
    private ScenarioOverrideRepository overrideRepository;
    
    @Autowired
    private FinBudgetRepository budgetRepository;
    
    @Autowired
    private FinAssumptionRepository assumptionRepository;

    private final String orgId = "test-org";
    @Test
    void shouldComputeDynamicCategoriesWithFormulas() {
        // 1. Setup Assumption: storage_rate = 0.5
        FinAssumption assumption = new FinAssumption();
        assumption.setOrganizationId(orgId);
        assumption.setProjectId(projId);
        assumption.setKey("storage_rate");
        assumption.setValue("0.5");
        assumptionRepository.save(assumption);

        // 2. Setup Budget with Formula: totalUsers * storage_rate
        FinBudget budget = new FinBudget();
        budget.setOrganizationId(orgId);
        budget.setProjectId(projId);
        budget.setCategory("Storage");
        budget.setFormula("totalUsers * storage_rate");
        budgetRepository.save(budget);
        
        // 3. Setup another Budget with a NEW dynamic category: "Cloud"
        FinBudget cloudBudget = new FinBudget();
        cloudBudget.setOrganizationId(orgId);
        cloudBudget.setProjectId(projId);
        cloudBudget.setCategory("Cloud");
        cloudBudget.setMonthlyAllocations(Map.of("2024-01", 150.0));
        budgetRepository.save(cloudBudget);

        // Compute
        computationService.recomputeFinancials(orgId, projId, "base", "test-user", List.of());

        FinFinancialSnapshot snapshot = snapshotRepository
                .findByOrganizationIdAndProjectIdAndScenarioIdAndMonth(orgId, projId, "base", "2024-01")
                .orElseThrow();

        // Total Users = 10 (from PRO tier in setup)
        // Storage Cost = 10 * 0.5 = 5.0
        // Cloud Cost = 150.0
        
        Map<String, Double> breakdown = snapshot.getBreakdown();
        assertEquals(5.0, breakdown.get("storageCost"), "Storage cost should be calculated via formula");
        assertEquals(150.0, breakdown.get("cloudCost"), "New dynamic category 'Cloud' should work");
        assertEquals(200.0, breakdown.get("baseCost"), "Base cost (10 * 20.0) should be correct");
    }
    private final String projId = "test-proj";

    @BeforeEach
    void setup() {
        salesPlanRepository.deleteAll();
        pricingModelRepository.deleteAll();
        snapshotRepository.deleteAll();
        scenarioRepository.deleteAll();
        overrideRepository.deleteAll();
        budgetRepository.deleteAll();
        assumptionRepository.deleteAll();

        // 1. Setup Base Pricing
        FinPricingModel pricing = new FinPricingModel();
        pricing.setOrganizationId(orgId);
        pricing.setProjectId(projId);
        pricing.setTier("PRO");
        pricing.setPrice(100.0);
        pricing.setCostPerUser(20.0);
        pricingModelRepository.save(pricing);

        // 2. Setup Base Sales Plan (Jan: 10 users)
        FinSalesPlan plan = new FinSalesPlan();
        plan.setOrganizationId(orgId);
        plan.setProjectId(projId);
        plan.setMonth("2024-01");
        Map<String, Integer> counts = new HashMap<>();
        counts.put("PRO", 10);
        plan.setUserCounts(counts);
        salesPlanRepository.save(plan);
    }

    @Test
    void shouldComputeBaseAndScenarioDifferently() {
        // Compute Base
        computationService.recomputeFinancials(orgId, projId, "base", "test-user", List.of());

        FinFinancialSnapshot baseSnapshot = snapshotRepository
                .findByOrganizationIdAndProjectIdAndScenarioIdAndMonth(orgId, projId, "base", "2024-01")
                .orElseThrow();

        // Base Revenue: 10 * 100 = 1000
        assertEquals(1000.0, baseSnapshot.getRevenue());

        // Create Scenario: Upside (increase users to 20)
        Scenario scenario = new Scenario();
        scenario.setOrganizationId(orgId);
        scenario.setProjectId(projId);
        scenario.setName("Upside Scenario");
        scenario = scenarioRepository.save(scenario);

        ScenarioOverride override = new ScenarioOverride();
        override.setScenarioId(scenario.getId());
        override.setMonth("2024-01");
        override.setPath("sales.PRO");
        override.setValue(20);
        overrideRepository.save(override);

        // Compute Scenario
        computationService.recomputeFinancials(orgId, projId, scenario.getId(), "test-user", List.of());

        FinFinancialSnapshot scenarioSnapshot = snapshotRepository
                .findByOrganizationIdAndProjectIdAndScenarioIdAndMonth(orgId, projId, scenario.getId(), "2024-01")
                .orElseThrow();

        // Scenario Revenue: 20 * 100 = 2000
        assertEquals(2000.0, scenarioSnapshot.getRevenue());

        // Ensure base snapshot was not overwritten
        FinFinancialSnapshot baseSnapshotPost = snapshotRepository
                .findByOrganizationIdAndProjectIdAndScenarioIdAndMonth(orgId, projId, "base", "2024-01")
                .orElseThrow();
        assertEquals(1000.0, baseSnapshotPost.getRevenue());
    }
}
