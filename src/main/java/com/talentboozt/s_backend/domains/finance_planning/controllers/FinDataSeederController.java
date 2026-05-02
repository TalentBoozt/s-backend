package com.talentboozt.s_backend.domains.finance_planning.controllers;

import com.talentboozt.s_backend.domains.finance_planning.models.*;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.*;
import com.talentboozt.s_backend.domains.finance_planning.security.annotations.RequiresFinPermission;
import com.talentboozt.s_backend.domains.finance_planning.security.rbac.FinPermission;
import com.talentboozt.s_backend.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/finance/seeder")
@RequiredArgsConstructor
public class FinDataSeederController {
    private final FinProjectRepository projectRepository;
    private final FinAssumptionRepository assumptionRepository;
    private final FinSalesPlanRepository salesPlanRepository;
    private final FinPricingModelRepository pricingModelRepository;
    private final FinBudgetRepository budgetRepository;

    @PostMapping("/seed")
    @RequiresFinPermission(value = FinPermission.WRITE_PROJECT, orgIdSource = "param", projectIdSource = "param")
    public ResponseEntity<ApiResponse<String>> seedData(
            @RequestParam(defaultValue = "default-project") String projectId,
            @RequestParam(defaultValue = "default-org") String organizationId) {

        // 0. Ensure Project exists
        if (projectRepository.findByOrganizationIdAndId(organizationId, projectId).isEmpty()) {
            FinProject project = FinProject.builder()
                    .id(projectId)
                    .organizationId(organizationId)
                    .name("Default Finance Model")
                    .description("Automatically created for initial workspace")
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            projectRepository.save(project);
        }

        // 1. Seed Assumptions
        if (assumptionRepository.findByOrganizationIdAndProjectId(organizationId, projectId).isEmpty()) {
            List<FinAssumption> assumptions = new ArrayList<>();
            assumptions.add(createAssumption(organizationId, projectId, "avgCourseFee", "50", "USD", "Revenue"));
            assumptions.add(createAssumption(organizationId, projectId, "storagePerCourseGB", "5", "GB", "Cost"));
            assumptions.add(createAssumption(organizationId, projectId, "storageCostPerGB", "0.02", "USD", "Cost"));
            assumptions.add(createAssumption(organizationId, projectId, "aiCostPerCourse", "0.5", "USD", "Cost"));
            assumptions.add(createAssumption(organizationId, projectId, "avgCoursesPerUser", "2", "Count", "General"));
            assumptions.add(createAssumption(organizationId, projectId, "paymentFeePercent", "2.9", "Percent", "Cost"));
            assumptionRepository.saveAll(assumptions);
        }

        // 2. Seed Pricing Models
        if (pricingModelRepository.findByOrganizationIdAndProjectId(organizationId, projectId).isEmpty()) {
            List<FinPricingModel> pricingModels = new ArrayList<>();
            pricingModels.add(createPricing(organizationId, projectId, "Free", 0.0, 0.0));
            pricingModels.add(createPricing(organizationId, projectId, "Pro", 29.0, 0.0));
            pricingModels.add(createPricing(organizationId, projectId, "Premium", 99.0, 0.0));
            pricingModels.add(createPricing(organizationId, projectId, "Enterprise", 499.0, 10.0));
            pricingModelRepository.saveAll(pricingModels);
        }

        // 3. Seed Sales Plans
        if (salesPlanRepository.findByOrganizationIdAndProjectId(organizationId, projectId).isEmpty()) {
            List<String> months = List.of("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
            List<FinSalesPlan> salesPlans = new ArrayList<>();
            for (String month : months) {
                FinSalesPlan plan = new FinSalesPlan();
                plan.setOrganizationId(organizationId);
                plan.setProjectId(projectId);
                plan.setMonth(month);
                plan.setUserCounts(Map.of("Free", 1000, "Pro", 50, "Premium", 10, "Enterprise", 2));
                plan.setCreatedAt(Instant.now());
                salesPlans.add(plan);
            }
            salesPlanRepository.saveAll(salesPlans);
        }

        // 4. Seed Budgets
        if (budgetRepository.findByOrganizationIdAndProjectId(organizationId, projectId).isEmpty()) {
            List<FinBudget> budgets = new ArrayList<>();
            
            Map<String, Double> hostingAllocations = Map.ofEntries(
                Map.entry("Jan", 500.0), Map.entry("Feb", 500.0), Map.entry("Mar", 500.0), Map.entry("Apr", 500.0),
                Map.entry("May", 500.0), Map.entry("Jun", 500.0), Map.entry("Jul", 500.0), Map.entry("Aug", 500.0),
                Map.entry("Sep", 500.0), Map.entry("Oct", 500.0), Map.entry("Nov", 500.0), Map.entry("Dec", 500.0)
            );
            budgets.add(createBudget(organizationId, projectId, "hosting", "fixed", hostingAllocations));
            
            Map<String, Double> marketingAllocations = Map.ofEntries(
                Map.entry("Jan", 2000.0), Map.entry("Feb", 2100.0), Map.entry("Mar", 2200.0), Map.entry("Apr", 2300.0),
                Map.entry("May", 2400.0), Map.entry("Jun", 2500.0), Map.entry("Jul", 2600.0), Map.entry("Aug", 2700.0),
                Map.entry("Sep", 2800.0), Map.entry("Oct", 2900.0), Map.entry("Nov", 3000.0), Map.entry("Dec", 3100.0)
            );
            budgets.add(createBudget(organizationId, projectId, "marketing", "variable", marketingAllocations));
            
            budgetRepository.saveAll(budgets);
        }

        return ResponseEntity.ok(ApiResponse.success("Data seeded successfully for project: " + projectId));
    }

    private FinAssumption createAssumption(String organizationId, String projId, String key, String value, String unit, String cat) {
        FinAssumption a = new FinAssumption();
        a.setOrganizationId(organizationId);
        a.setProjectId(projId);
        a.setKey(key);
        a.setValue(value);
        a.setUnit(unit);
        a.setCategory(cat);
        a.setCreatedAt(Instant.now());
        return a;
    }

    private FinPricingModel createPricing(String organizationId, String projId, String tier, Double price, Double commission) {
        FinPricingModel p = new FinPricingModel();
        p.setOrganizationId(organizationId);
        p.setProjectId(projId);
        p.setTier(tier);
        p.setPrice(price);
        p.setCommissionPercent(commission);
        p.setEffectiveDate(Instant.now());
        return p;
    }

    private FinBudget createBudget(String organizationId, String projId, String cat, String type, Map<String, Double> allocations) {
        FinBudget b = new FinBudget();
        b.setOrganizationId(organizationId);
        b.setProjectId(projId);
        b.setCategory(cat);
        b.setType(type);
        b.setMonthlyAllocations(allocations);
        b.setCreatedAt(Instant.now());
        return b;
    }
}
