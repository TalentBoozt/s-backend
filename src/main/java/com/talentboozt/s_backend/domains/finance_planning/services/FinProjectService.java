package com.talentboozt.s_backend.domains.finance_planning.services;

import com.talentboozt.s_backend.domains.finance_planning.models.*;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.*;
import com.talentboozt.s_backend.domains.finance_planning.security.rbac.ProjectRole;
import com.talentboozt.s_backend.shared.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FinProjectService {
    private final FinProjectRepository projectRepository;
    private final FinAssumptionRepository assumptionRepository;
    private final FinProjectMemberRepository memberRepository;
    private final FinFinancialSnapshotRepository snapshotRepository;
    private final FinSalesPlanRepository salesPlanRepository;
    private final FinPricingModelRepository pricingModelRepository;
    private final FinBudgetRepository budgetRepository;
    private final SecurityUtils securityUtils;

    public List<FinProject> getProjectsByOrganization(String organizationId) {
        return projectRepository.findByOrganizationId(organizationId);
    }

    public Map<String, Object> getPortfolioSummary(String organizationId) {
        List<FinFinancialSnapshot> snapshots = snapshotRepository.findByOrganizationId(organizationId);

        double totalRevenue = 0;
        double totalCost = 0;
        double totalProfit = 0;

        for (FinFinancialSnapshot s : snapshots) {
            totalRevenue += s.getRevenue() != null ? s.getRevenue() : 0;
            totalCost += s.getCost() != null ? s.getCost() : 0;
            totalProfit += s.getProfit() != null ? s.getProfit() : 0;
        }

        java.util.Map<String, Object> summary = new java.util.HashMap<>();
        summary.put("totalRevenue", totalRevenue);
        summary.put("totalCost", totalCost);
        summary.put("totalProfit", totalProfit);
        summary.put("projectCount", projectRepository.countByOrganizationId(organizationId));

        return summary;
    }

    public Optional<FinProject> getProject(String organizationId, String projectId) {
        return projectRepository.findByOrganizationIdAndId(organizationId, projectId);
    }

    public FinProject createProject(FinProject project) {
        String userId = securityUtils.getCurrentUserId();

        if (project.getCreatedAt() == null) {
            project.setCreatedAt(Instant.now());
        }
        project.setUpdatedAt(Instant.now());
        project.setOwnerId(userId);
        project.setStatus("DRAFT");

        FinProject saved = projectRepository.save(project);

        // 1. Initialize with basic templates
        initializeProjectTemplate(saved);

        // 2. Add owner as member
        initializeProjectMember(saved, userId, ProjectRole.PROJECT_OWNER);

        return saved;
    }

    private void initializeProjectMember(FinProject project, String userId, ProjectRole role) {
        FinProjectMember member = FinProjectMember.builder()
                .projectId(project.getId())
                .userId(userId)
                .role(role)
                .joinedAt(Instant.now())
                .build();
        memberRepository.save(member);
    }

    private void initializeProjectTemplate(FinProject project) {
        String type = project.getType() != null ? project.getType().toUpperCase() : "SAAS";
        String orgId = project.getOrganizationId();
        String projectId = project.getId();

        // 1. Baseline Assumptions
        saveAssumption(project, "avg_course_fee", "50", "currency", "revenue");
        saveAssumption(project, "storage_per_course_gb", "5", "gb", "cost");
        saveAssumption(project, "storage_cost_per_gb", "0.02", "currency", "cost");
        saveAssumption(project, "ai_cost_per_course", "0.5", "currency", "cost");
        saveAssumption(project, "payment_fee_percent", "2.9", "percentage", "cost");

        // 2. Pricing Models
        String[] tiers = { "Free", "Pro", "Premium", "Enterprise" };
        double[] prices = { 0, 29, 99, 499 };
        double[] commissions = { 0, 0, 0, 10 };

        for (int i = 0; i < tiers.length; i++) {
            FinPricingModel pm = new FinPricingModel();
            pm.setOrganizationId(orgId);
            pm.setProjectId(projectId);
            pm.setTier(tiers[i]);
            pm.setPrice(prices[i]);
            pm.setCommissionPercent(commissions[i]);
            pm.setEffectiveDate(Instant.now());
            pricingModelRepository.save(pm);
        }

        // 3. Initial Sales Plan (Month-by-month for 1st year)
        String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
        for (String month : months) {
            FinSalesPlan sp = new FinSalesPlan();
            sp.setOrganizationId(orgId);
            sp.setProjectId(projectId);
            sp.setMonth(month);
            sp.setGrowthRate(0.1);
            Map<String, Integer> userCounts = new HashMap<>();
            userCounts.put("Free", 1000);
            userCounts.put("Pro", 50);
            userCounts.put("Premium", 10);
            userCounts.put("Enterprise", 2);
            sp.setUserCounts(userCounts);
            sp.setCreatedAt(Instant.now());
            salesPlanRepository.save(sp);
        }

        // 4. Initial Budget
        String[] fixedCats = { "hosting", "domain" };
        double[] fixedVals = { 500, 50 };
        for (int i = 0; i < fixedCats.length; i++) {
            FinBudget b = new FinBudget();
            b.setOrganizationId(orgId);
            b.setProjectId(projectId);
            b.setType("fixed");
            b.setCategory(fixedCats[i]);
            Map<String, Double> allocations = new HashMap<>();
            for (String month : months)
                allocations.put(month, fixedVals[i]);
            b.setMonthlyAllocations(allocations);
            b.setCreatedAt(Instant.now());
            budgetRepository.save(b);
        }

        String[] varCats = { "marketing", "personnel" };
        double[] varVals = { 2000, 15000 };
        for (int i = 0; i < varCats.length; i++) {
            FinBudget b = new FinBudget();
            b.setOrganizationId(orgId);
            b.setProjectId(projectId);
            b.setType("variable");
            b.setCategory(varCats[i]);
            Map<String, Double> allocations = new HashMap<>();
            for (String month : months)
                allocations.put(month, varVals[i]);
            b.setMonthlyAllocations(allocations);
            b.setCreatedAt(Instant.now());
            budgetRepository.save(b);
        }
    }

    private void saveAssumption(FinProject project, String key, String value, String unit, String category) {
        FinAssumption a = new FinAssumption();
        a.setOrganizationId(project.getOrganizationId());
        a.setProjectId(project.getId());
        a.setKey(key);
        a.setValue(value);
        a.setUnit(unit);
        a.setCategory(category);
        a.setCreatedAt(Instant.now());
        assumptionRepository.save(a);
    }

    public FinProject updateProject(String organizationId, String projectId, FinProject projectDetails) {
        return projectRepository.findByOrganizationIdAndId(organizationId, projectId)
                .map(project -> {
                    project.setName(projectDetails.getName());
                    project.setDescription(projectDetails.getDescription());
                    project.setUpdatedAt(Instant.now());
                    return projectRepository.save(project);
                })
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    public FinProject updateProjectStatus(String organizationId, String projectId, String status) {
        return projectRepository.findByOrganizationIdAndId(organizationId, projectId)
                .map(project -> {
                    project.setStatus(status.toUpperCase());
                    project.setUpdatedAt(Instant.now());
                    return projectRepository.save(project);
                })
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    public void deleteProject(String organizationId, String projectId) {
        projectRepository.deleteById(projectId);
    }
}
