package com.talentboozt.s_backend.domains.finance.services;

import com.talentboozt.s_backend.domains.finance.events.FinancialsChangedEvent;
import com.talentboozt.s_backend.domains.finance.models.*;
import com.talentboozt.s_backend.domains.finance.repository.mongodb.*;
import com.talentboozt.s_backend.domains.finance.scenario.resolver.ScenarioResolver;
import com.talentboozt.s_backend.domains.finance.analytics.service.FormulaEngine;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Duration;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class FinFinancialComputationServiceTest {

    private FinAssumptionRepository assumptionRepository;
    private FinSalesPlanRepository salesPlanRepository;
    private FinPricingModelRepository pricingModelRepository;
    private FinBudgetRepository budgetRepository;
    private FinFinancialSnapshotRepository financialSnapshotRepository;
    private ApplicationEventPublisher eventPublisher;
    private ScenarioResolver scenarioResolver;
    private FormulaEngine formulaEngine;
    private MeterRegistry meterRegistry;
    private Timer timer;
    private Counter successCounter;
    private Counter failureCounter;

    private FinFinancialComputationService service;

    @BeforeEach
    void setUp() {
        assumptionRepository = mock(FinAssumptionRepository.class);
        salesPlanRepository = mock(FinSalesPlanRepository.class);
        pricingModelRepository = mock(FinPricingModelRepository.class);
        budgetRepository = mock(FinBudgetRepository.class);
        financialSnapshotRepository = mock(FinFinancialSnapshotRepository.class);
        eventPublisher = mock(ApplicationEventPublisher.class);
        scenarioResolver = mock(ScenarioResolver.class);
        formulaEngine = mock(FormulaEngine.class);
        meterRegistry = mock(MeterRegistry.class);

        timer = mock(Timer.class);
        successCounter = mock(Counter.class);
        failureCounter = mock(Counter.class);

        when(meterRegistry.timer(anyString(), any(String[].class))).thenReturn(timer);
        when(meterRegistry.counter(eq("finance.computation.success"), any(String[].class))).thenReturn(successCounter);
        when(meterRegistry.counter(eq("finance.computation.failure"), any(String[].class))).thenReturn(failureCounter);

        service = new FinFinancialComputationService(
                assumptionRepository,
                salesPlanRepository,
                pricingModelRepository,
                budgetRepository,
                financialSnapshotRepository,
                eventPublisher,
                scenarioResolver,
                formulaEngine,
                meterRegistry
        );
    }

    @Test
    void computeOnly_shouldReturnEmptyListWhenSalesPlansOrPricingModelsIsEmpty() {
        // Arrange
        List<FinAssumption> assumptions = new ArrayList<>();
        List<FinSalesPlan> salesPlans = new ArrayList<>();
        List<FinPricingModel> pricingModels = new ArrayList<>();
        List<FinBudget> budgets = new ArrayList<>();

        // Act
        List<FinFinancialSnapshot> result = service.computeOnly(assumptions, salesPlans, pricingModels, budgets);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void computeOnly_shouldCalculateRevenueCostProfitAndBreakdown() {
        // Arrange
        List<FinAssumption> assumptions = new ArrayList<>();
        FinAssumption assumption = new FinAssumption();
        assumption.setKey("taxRate");
        assumption.setValue("0.1");
        assumptions.add(assumption);

        List<FinSalesPlan> salesPlans = new ArrayList<>();
        FinSalesPlan salesPlan = new FinSalesPlan();
        salesPlan.setMonth("2026-06");
        Map<String, Integer> userCounts = new HashMap<>();
        userCounts.put("premium", 100);
        salesPlan.setUserCounts(userCounts);
        salesPlans.add(salesPlan);

        List<FinPricingModel> pricingModels = new ArrayList<>();
        FinPricingModel pricingModel = new FinPricingModel();
        pricingModel.setTier("premium");
        pricingModel.setPrice(10.0);
        pricingModel.setCostPerUser(2.0);
        pricingModels.add(pricingModel);

        List<FinBudget> budgets = new ArrayList<>();
        FinBudget budget = new FinBudget();
        budget.setCategory("marketing");
        budget.setFormula("totalUsers * 0.5");
        budgets.add(budget);

        when(formulaEngine.calculate(eq("totalUsers * 0.5"), anyMap())).thenReturn(50.0);

        // Act
        List<FinFinancialSnapshot> result = service.computeOnly(assumptions, salesPlans, pricingModels, budgets);

        // Assert
        assertThat(result).hasSize(1);
        FinFinancialSnapshot snapshot = result.get(0);
        assertThat(snapshot.getMonth()).isEqualTo("2026-06");
        // Revenue = 10.0 * 100 = 1000.0
        assertThat(snapshot.getRevenue()).isEqualTo(1000.0);
        // Cost = baseCost (2.0 * 100 = 200.0) + marketingCost (50.0) = 250.0
        assertThat(snapshot.getCost()).isEqualTo(250.0);
        // Profit = 1000.0 - 250.0 = 750.0
        assertThat(snapshot.getProfit()).isEqualTo(750.0);
        assertThat(snapshot.getBreakdown()).containsEntry("baseCost", 200.0);
        assertThat(snapshot.getBreakdown()).containsEntry("marketingCost", 50.0);
    }

    @Test
    void computeRevenue_shouldReturnZeroWhenUserCountsIsNull() {
        // Arrange
        List<FinAssumption> assumptions = new ArrayList<>();
        List<FinSalesPlan> salesPlans = new ArrayList<>();
        FinSalesPlan salesPlan = new FinSalesPlan();
        salesPlan.setMonth("2026-06");
        salesPlan.setUserCounts(null);
        salesPlans.add(salesPlan);

        List<FinPricingModel> pricingModels = new ArrayList<>();
        FinPricingModel pricingModel = new FinPricingModel();
        pricingModel.setTier("premium");
        pricingModel.setPrice(10.0);
        pricingModels.add(pricingModel);

        List<FinBudget> budgets = new ArrayList<>();

        // Act
        List<FinFinancialSnapshot> result = service.computeOnly(assumptions, salesPlans, pricingModels, budgets);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRevenue()).isEqualTo(0.0);
    }

    @Test
    void computeRevenue_shouldExcludeUnrecognizedPricingTiers() {
        // Arrange
        List<FinAssumption> assumptions = new ArrayList<>();
        List<FinSalesPlan> salesPlans = new ArrayList<>();
        FinSalesPlan salesPlan = new FinSalesPlan();
        salesPlan.setMonth("2026-06");
        Map<String, Integer> userCounts = new HashMap<>();
        userCounts.put("unrecognized", 100);
        salesPlan.setUserCounts(userCounts);
        salesPlans.add(salesPlan);

        List<FinPricingModel> pricingModels = new ArrayList<>();
        FinPricingModel pricingModel = new FinPricingModel();
        pricingModel.setTier("premium");
        pricingModel.setPrice(10.0);
        pricingModels.add(pricingModel);

        List<FinBudget> budgets = new ArrayList<>();

        // Act
        List<FinFinancialSnapshot> result = service.computeOnly(assumptions, salesPlans, pricingModels, budgets);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRevenue()).isEqualTo(0.0);
    }

    @Test
    void prepareBaseVariables_shouldFallbackToZeroForNonNumericAssumptions() {
        // Arrange
        List<FinAssumption> assumptions = new ArrayList<>();
        FinAssumption assumption = new FinAssumption();
        assumption.setKey("invalidAssumption");
        assumption.setValue("not-a-number");
        assumptions.add(assumption);

        List<FinSalesPlan> salesPlans = new ArrayList<>();
        FinSalesPlan salesPlan = new FinSalesPlan();
        salesPlan.setMonth("2026-06");
        salesPlan.setUserCounts(new HashMap<>());
        salesPlans.add(salesPlan);

        List<FinPricingModel> pricingModels = new ArrayList<>();
        FinPricingModel pricingModel = new FinPricingModel();
        pricingModel.setTier("premium");
        pricingModel.setPrice(10.0);
        pricingModels.add(pricingModel);

        List<FinBudget> budgets = new ArrayList<>();
        FinBudget budget = new FinBudget();
        budget.setCategory("marketing");
        budget.setFormula("invalidAssumption * 10");
        budgets.add(budget);

        // Act
        service.computeOnly(assumptions, salesPlans, pricingModels, budgets);

        // Assert - verify invalidAssumption was registered as 0.0 inside formula engine variables map
        ArgumentCaptor<Map<String, Double>> captor = ArgumentCaptor.forClass(Map.class);
        verify(formulaEngine, times(1)).calculate(anyString(), captor.capture());
        Map<String, Double> passedVariables = captor.getValue();
        assertThat(passedVariables).containsEntry("invalidAssumption", 0.0);
    }

    @Test
    void computeCosts_shouldEvaluateDynamicBudgetFormula() {
        // Arrange
        List<FinAssumption> assumptions = new ArrayList<>();
        List<FinSalesPlan> salesPlans = new ArrayList<>();
        FinSalesPlan salesPlan = new FinSalesPlan();
        salesPlan.setMonth("2026-06");
        Map<String, Integer> userCounts = new HashMap<>();
        userCounts.put("premium", 50);
        salesPlan.setUserCounts(userCounts);
        salesPlans.add(salesPlan);

        List<FinPricingModel> pricingModels = new ArrayList<>();
        FinPricingModel pm = new FinPricingModel();
        pm.setTier("premium");
        pm.setPrice(10.0);
        pm.setCostPerUser(2.0);
        pricingModels.add(pm);

        List<FinBudget> budgets = new ArrayList<>();
        FinBudget budget = new FinBudget();
        budget.setCategory("ops");
        budget.setFormula("totalUsers * 2.0");
        budgets.add(budget);

        when(formulaEngine.calculate(eq("totalUsers * 2.0"), anyMap())).thenReturn(100.0);

        // Act
        List<FinFinancialSnapshot> result = service.computeOnly(assumptions, salesPlans, pricingModels, budgets);

        // Assert
        assertThat(result.get(0).getBreakdown()).containsEntry("opsCost", 100.0);
    }

    @Test
    void computeCosts_shouldFallbackToMonthlyAllocationsWhenFormulaFails() {
        // Arrange
        List<FinAssumption> assumptions = new ArrayList<>();
        List<FinSalesPlan> salesPlans = new ArrayList<>();
        FinSalesPlan salesPlan = new FinSalesPlan();
        salesPlan.setMonth("2026-06");
        salesPlan.setUserCounts(new HashMap<>());
        salesPlans.add(salesPlan);

        List<FinPricingModel> pricingModels = new ArrayList<>();
        FinPricingModel pm = new FinPricingModel();
        pm.setTier("premium");
        pm.setPrice(10.0);
        pm.setCostPerUser(2.0);
        pricingModels.add(pm);

        List<FinBudget> budgets = new ArrayList<>();
        FinBudget budget = new FinBudget();
        budget.setCategory("ops");
        budget.setFormula("totalUsers * 2.0");
        Map<String, Double> allocations = new HashMap<>();
        allocations.put("2026-06", 50.0);
        budget.setMonthlyAllocations(allocations);
        budgets.add(budget);

        when(formulaEngine.calculate(eq("totalUsers * 2.0"), anyMap())).thenThrow(new RuntimeException("Formula error"));

        // Act
        List<FinFinancialSnapshot> result = service.computeOnly(assumptions, salesPlans, pricingModels, budgets);

        // Assert
        assertThat(result.get(0).getBreakdown()).containsEntry("opsCost", 50.0);
    }

    @Test
    void computeCosts_shouldUseMonthlyAllocationsWhenNoFormulaExists() {
        // Arrange
        List<FinAssumption> assumptions = new ArrayList<>();
        List<FinSalesPlan> salesPlans = new ArrayList<>();
        FinSalesPlan salesPlan = new FinSalesPlan();
        salesPlan.setMonth("2026-06");
        salesPlan.setUserCounts(new HashMap<>());
        salesPlans.add(salesPlan);

        List<FinPricingModel> pricingModels = new ArrayList<>();
        FinPricingModel pm = new FinPricingModel();
        pm.setTier("premium");
        pm.setPrice(10.0);
        pm.setCostPerUser(2.0);
        pricingModels.add(pm);

        List<FinBudget> budgets = new ArrayList<>();
        FinBudget budget = new FinBudget();
        budget.setCategory("ops");
        budget.setFormula(null);
        Map<String, Double> allocations = new HashMap<>();
        allocations.put("2026-06", 45.0);
        budget.setMonthlyAllocations(allocations);
        budgets.add(budget);

        // Act
        List<FinFinancialSnapshot> result = service.computeOnly(assumptions, salesPlans, pricingModels, budgets);

        // Assert
        assertThat(result.get(0).getBreakdown()).containsEntry("opsCost", 45.0);
    }

    @Test
    void recomputeFinancials_shouldComputeAllMonthsForGlobalChanges() {
        // Arrange
        ScenarioResolver.EffectiveProjectState state = new ScenarioResolver.EffectiveProjectState();
        state.setAssumptions(new ArrayList<>());
        state.setPricingModels(new ArrayList<>());
        state.setBudgets(new ArrayList<>());

        List<FinSalesPlan> salesPlans = new ArrayList<>();
        FinSalesPlan p1 = new FinSalesPlan(); p1.setMonth("2026-06");
        FinSalesPlan p2 = new FinSalesPlan(); p2.setMonth("2026-07");
        salesPlans.add(p1);
        salesPlans.add(p2);
        state.setSalesPlans(salesPlans);

        when(scenarioResolver.resolveState(eq("base"), eq("org-123"), eq("proj-456"))).thenReturn(state);
        when(financialSnapshotRepository.findByOrganizationIdAndProjectIdAndScenarioIdAndMonth(any(), any(), any(), any()))
                .thenReturn(Optional.empty());

        // Act
        service.recomputeFinancials("org-123", "proj-456", "base", "user-1", List.of("assumption.taxRate"), null);

        // Assert - global change forces recomputation of both months
        verify(financialSnapshotRepository, times(1))
                .findByOrganizationIdAndProjectIdAndScenarioIdAndMonth("org-123", "proj-456", "base", "2026-06");
        verify(financialSnapshotRepository, times(1))
                .findByOrganizationIdAndProjectIdAndScenarioIdAndMonth("org-123", "proj-456", "base", "2026-07");
    }

    @Test
    void recomputeFinancials_shouldComputeOnlyAffectedMonthsForLocalChanges() {
        // Arrange
        ScenarioResolver.EffectiveProjectState state = new ScenarioResolver.EffectiveProjectState();
        state.setAssumptions(new ArrayList<>());
        state.setPricingModels(new ArrayList<>());
        state.setBudgets(new ArrayList<>());

        List<FinSalesPlan> salesPlans = new ArrayList<>();
        FinSalesPlan p1 = new FinSalesPlan(); p1.setMonth("2026-06");
        FinSalesPlan p2 = new FinSalesPlan(); p2.setMonth("2026-07");
        salesPlans.add(p1);
        salesPlans.add(p2);
        state.setSalesPlans(salesPlans);

        when(scenarioResolver.resolveState(eq("base"), eq("org-123"), eq("proj-456"))).thenReturn(state);
        when(financialSnapshotRepository.findByOrganizationIdAndProjectIdAndScenarioIdAndMonth(any(), any(), any(), any()))
                .thenReturn(Optional.empty());

        // Act - local change to specific month
        service.recomputeFinancials("org-123", "proj-456", "base", "user-1", List.of("sales.premium"), List.of("2026-06"));

        // Assert - only recomputed 2026-06
        verify(financialSnapshotRepository, times(1))
                .findByOrganizationIdAndProjectIdAndScenarioIdAndMonth("org-123", "proj-456", "base", "2026-06");
        verify(financialSnapshotRepository, never())
                .findByOrganizationIdAndProjectIdAndScenarioIdAndMonth("org-123", "proj-456", "base", "2026-07");
    }

    @Test
    void recomputeFinancials_shouldPublishEventAndUpdateMetricsOnSuccess() {
        // Arrange
        ScenarioResolver.EffectiveProjectState state = new ScenarioResolver.EffectiveProjectState();
        state.setAssumptions(new ArrayList<>());
        state.setPricingModels(new ArrayList<>());
        state.setBudgets(new ArrayList<>());
        state.setSalesPlans(new ArrayList<>());

        when(scenarioResolver.resolveState(eq("base"), eq("org-123"), eq("proj-456"))).thenReturn(state);

        // Act
        service.recomputeFinancials("org-123", "proj-456");

        // Assert
        verify(timer, times(1)).record(any(Duration.class));
        verify(successCounter, times(1)).increment();
        verify(eventPublisher, times(1)).publishEvent(any(FinancialsChangedEvent.class));
    }

    @Test
    void recomputeFinancials_shouldIncrementFailureMetricAndRethrowOnException() {
        // Arrange
        when(scenarioResolver.resolveState(any(), any(), any())).thenThrow(new RuntimeException("DB Connection failed"));

        // Act & Assert
        assertThatThrownBy(() -> service.recomputeFinancials("org-123", "proj-456"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("DB Connection failed");

        verify(failureCounter, times(1)).increment();
    }
}
