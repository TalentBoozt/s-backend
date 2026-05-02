package com.talentboozt.s_backend.domains.finance_planning.services;

import com.talentboozt.s_backend.domains.finance_planning.models.FinFinancialSnapshot;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.FinFinancialSnapshotRepository;
import com.talentboozt.s_backend.domains.finance_planning.models.FinAssumption;
import com.talentboozt.s_backend.domains.finance_planning.models.FinProject;
import com.talentboozt.s_backend.domains.finance_planning.models.FinProjectMember;
import com.talentboozt.s_backend.domains.finance_planning.security.rbac.ProjectRole;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.FinAssumptionRepository;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.FinProjectMemberRepository;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.FinProjectRepository;
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
        String type = project.getType() != null ? project.getType().toUpperCase() : "GENERAL";
        
        // Baseline assumptions for all
        saveAssumption(project, "base_currency", "USD", "string", "general");
        saveAssumption(project, "corporate_tax", "25", "percentage", "general");

        if ("SAAS".equals(type)) {
            saveAssumption(project, "churn_rate", "3", "percentage", "revenue");
            saveAssumption(project, "cac", "100", "currency", "marketing");
            saveAssumption(project, "ltv_months", "36", "count", "revenue");
        } else if ("ECOMMERCE".equals(type)) {
            saveAssumption(project, "avg_order_value", "50", "currency", "revenue");
            saveAssumption(project, "cogs_percentage", "40", "percentage", "cost");
            saveAssumption(project, "shipping_cost_per_order", "5", "currency", "cost");
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
