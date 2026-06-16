package com.talentboozt.s_backend.domains.finance.services;

import com.talentboozt.s_backend.domains.finance.models.*;
import com.talentboozt.s_backend.domains.finance.repository.mongodb.*;
import com.talentboozt.s_backend.domains.finance.security.rbac.ProjectRole;
import com.talentboozt.s_backend.shared.security.utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FinProjectServiceTest {

    private FinProjectRepository projectRepository;
    private FinAssumptionRepository assumptionRepository;
    private FinProjectMemberRepository memberRepository;
    private FinFinancialSnapshotRepository snapshotRepository;
    private FinSalesPlanRepository salesPlanRepository;
    private FinPricingModelRepository pricingModelRepository;
    private FinBudgetRepository budgetRepository;
    private SecurityUtils securityUtils;

    private FinProjectService service;

    @BeforeEach
    void setUp() {
        projectRepository = mock(FinProjectRepository.class);
        assumptionRepository = mock(FinAssumptionRepository.class);
        memberRepository = mock(FinProjectMemberRepository.class);
        snapshotRepository = mock(FinFinancialSnapshotRepository.class);
        salesPlanRepository = mock(FinSalesPlanRepository.class);
        pricingModelRepository = mock(FinPricingModelRepository.class);
        budgetRepository = mock(FinBudgetRepository.class);
        securityUtils = mock(SecurityUtils.class);

        service = new FinProjectService(
                projectRepository,
                assumptionRepository,
                memberRepository,
                snapshotRepository,
                salesPlanRepository,
                pricingModelRepository,
                budgetRepository,
                securityUtils
        );
    }

    @Test
    void getProjectsByOrganization_shouldReturnProjects() {
        // Arrange
        String orgId = "org-1";
        List<FinProject> expected = List.of(new FinProject(), new FinProject());
        when(projectRepository.findByOrganizationId(orgId)).thenReturn(expected);

        // Act
        List<FinProject> result = service.getProjectsByOrganization(orgId);

        // Assert
        assertThat(result).isSameAs(expected);
    }

    @Test
    void getPortfolioSummary_shouldAggregateSnapshotsCorrectly() {
        // Arrange
        String orgId = "org-1";

        FinFinancialSnapshot s1 = new FinFinancialSnapshot();
        s1.setRevenue(1000.0);
        s1.setCost(300.0);
        s1.setProfit(700.0);

        FinFinancialSnapshot s2 = new FinFinancialSnapshot();
        s2.setRevenue(2000.0);
        s2.setCost(800.0);
        s2.setProfit(1200.0);

        FinFinancialSnapshot s3 = new FinFinancialSnapshot(); // testing null values safety
        s3.setRevenue(null);
        s3.setCost(null);
        s3.setProfit(null);

        when(snapshotRepository.findByOrganizationId(orgId)).thenReturn(List.of(s1, s2, s3));
        when(projectRepository.countByOrganizationId(orgId)).thenReturn(5L);

        // Act
        Map<String, Object> summary = service.getPortfolioSummary(orgId);

        // Assert
        assertThat(summary).containsEntry("totalRevenue", 3000.0);
        assertThat(summary).containsEntry("totalCost", 1100.0);
        assertThat(summary).containsEntry("totalProfit", 1900.0);
        assertThat(summary).containsEntry("projectCount", 5L);
    }

    @Test
    void getProject_shouldReturnProjectWhenFound() {
        // Arrange
        String orgId = "org-1";
        String projectId = "proj-1";
        FinProject expected = new FinProject();
        when(projectRepository.findByOrganizationIdAndId(orgId, projectId)).thenReturn(Optional.of(expected));

        // Act
        Optional<FinProject> result = service.getProject(orgId, projectId);

        // Assert
        assertThat(result).contains(expected);
    }

    @Test
    void createProject_shouldSaveProjectWithTemplateAndOwner() {
        // Arrange
        String userId = "user-123";
        FinProject project = new FinProject();
        project.setId("proj-1");
        project.setOrganizationId("org-1");
        project.setType("SAAS");

        when(securityUtils.getCurrentUserId()).thenReturn(userId);
        when(projectRepository.save(any(FinProject.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        FinProject created = service.createProject(project);

        // Assert
        assertThat(created.getOwnerId()).isEqualTo(userId);
        assertThat(created.getStatus()).isEqualTo("DRAFT");
        assertThat(created.getCreatedAt()).isNotNull();
        assertThat(created.getUpdatedAt()).isNotNull();

        // Verify owner is added as project owner member
        ArgumentCaptor<FinProjectMember> memberCaptor = ArgumentCaptor.forClass(FinProjectMember.class);
        verify(memberRepository, times(1)).save(memberCaptor.capture());
        FinProjectMember member = memberCaptor.getValue();
        assertThat(member.getProjectId()).isEqualTo("proj-1");
        assertThat(member.getUserId()).isEqualTo(userId);
        assertThat(member.getRole()).isEqualTo(ProjectRole.PROJECT_OWNER);

        // Verify templates are initialized
        verify(assumptionRepository, times(5)).save(any(FinAssumption.class));
        verify(pricingModelRepository, times(4)).save(any(FinPricingModel.class));
        verify(salesPlanRepository, times(12)).save(any(FinSalesPlan.class));
        verify(budgetRepository, times(4)).save(any(FinBudget.class));
    }

    @Test
    void updateProject_shouldUpdateDetailsWhenProjectExists() {
        // Arrange
        String orgId = "org-1";
        String projectId = "proj-1";
        FinProject existing = new FinProject();
        existing.setId(projectId);
        existing.setOrganizationId(orgId);
        existing.setName("Old Name");
        existing.setDescription("Old Description");

        FinProject details = new FinProject();
        details.setName("New Name");
        details.setDescription("New Description");

        when(projectRepository.findByOrganizationIdAndId(orgId, projectId)).thenReturn(Optional.of(existing));
        when(projectRepository.save(any(FinProject.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        FinProject result = service.updateProject(orgId, projectId, details);

        // Assert
        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getDescription()).isEqualTo("New Description");
        assertThat(result.getUpdatedAt()).isNotNull();
        verify(projectRepository, times(1)).save(existing);
    }

    @Test
    void updateProject_shouldThrowExceptionWhenProjectDoesNotExist() {
        // Arrange
        String orgId = "org-1";
        String projectId = "proj-1";
        when(projectRepository.findByOrganizationIdAndId(orgId, projectId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.updateProject(orgId, projectId, new FinProject()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Project not found");
    }

    @Test
    void updateProjectStatus_shouldUpdateStatusWhenProjectExists() {
        // Arrange
        String orgId = "org-1";
        String projectId = "proj-1";
        FinProject existing = new FinProject();
        existing.setId(projectId);
        existing.setOrganizationId(orgId);
        existing.setStatus("DRAFT");

        when(projectRepository.findByOrganizationIdAndId(orgId, projectId)).thenReturn(Optional.of(existing));
        when(projectRepository.save(any(FinProject.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        FinProject result = service.updateProjectStatus(orgId, projectId, "approved");

        // Assert
        assertThat(result.getStatus()).isEqualTo("APPROVED");
        assertThat(result.getUpdatedAt()).isNotNull();
        verify(projectRepository, times(1)).save(existing);
    }

    @Test
    void updateProjectStatus_shouldThrowExceptionWhenProjectDoesNotExist() {
        // Arrange
        String orgId = "org-1";
        String projectId = "proj-1";
        when(projectRepository.findByOrganizationIdAndId(orgId, projectId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.updateProjectStatus(orgId, projectId, "APPROVED"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Project not found");
    }

    @Test
    void deleteProject_shouldDeleteProject() {
        // Arrange
        String orgId = "org-1";
        String projectId = "proj-1";

        // Act
        service.deleteProject(orgId, projectId);

        // Assert
        verify(projectRepository, times(1)).deleteById(projectId);
    }
}
