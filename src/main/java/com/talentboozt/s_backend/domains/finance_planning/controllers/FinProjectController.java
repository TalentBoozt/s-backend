package com.talentboozt.s_backend.domains.finance_planning.controllers;

import com.talentboozt.s_backend.domains.finance_planning.security.annotations.RequiresFinPermission;
import com.talentboozt.s_backend.domains.finance_planning.models.FinProject;
import com.talentboozt.s_backend.domains.finance_planning.security.rbac.FinPermission;
import com.talentboozt.s_backend.domains.finance_planning.services.FinProjectService;
import com.talentboozt.s_backend.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/finance/projects")
@RequiredArgsConstructor
public class FinProjectController {
    private final FinProjectService projectService;

    @GetMapping("/portfolio-summary")
    @RequiresFinPermission(FinPermission.READ_PROJECT)
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPortfolioSummary(
            @RequestHeader("X-Organization-Id") String organizationId) {
        return ResponseEntity.ok(ApiResponse.success(projectService.getPortfolioSummary(organizationId)));
    }

    @GetMapping
    @RequiresFinPermission(FinPermission.READ_PROJECT)
    public ResponseEntity<ApiResponse<List<FinProject>>> getProjects(
            @RequestHeader("X-Organization-Id") String organizationId) {
        List<FinProject> projects = projectService.getProjectsByOrganization(organizationId);
        return ResponseEntity.ok(ApiResponse.success(projects));
    }

    @GetMapping("/{projectId}")
    @RequiresFinPermission(FinPermission.READ_PROJECT)
    public ResponseEntity<ApiResponse<FinProject>> getProject(
            @RequestHeader("X-Organization-Id") String organizationId,
            @PathVariable String projectId) {
        return projectService.getProject(organizationId, projectId)
                .map(project -> ResponseEntity.ok(ApiResponse.success(project)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @RequiresFinPermission(FinPermission.WRITE_PROJECT)
    public ResponseEntity<ApiResponse<FinProject>> createProject(
            @RequestHeader("X-Organization-Id") String organizationId,
            @RequestBody FinProject project) {
        project.setOrganizationId(organizationId);
        FinProject created = projectService.createProject(project);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    @PutMapping("/{projectId}")
    @RequiresFinPermission(FinPermission.WRITE_PROJECT)
    public ResponseEntity<ApiResponse<FinProject>> updateProject(
            @RequestHeader("X-Organization-Id") String organizationId,
            @PathVariable String projectId,
            @RequestBody FinProject project) {
        FinProject updated = projectService.updateProject(organizationId, projectId, project);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @PatchMapping("/{projectId}/status")
    @RequiresFinPermission(FinPermission.WRITE_PROJECT)
    public ResponseEntity<ApiResponse<FinProject>> updateStatus(
            @RequestHeader("X-Organization-Id") String organizationId,
            @PathVariable String projectId,
            @RequestParam String status) {
        FinProject updated = projectService.updateProjectStatus(organizationId, projectId, status);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @DeleteMapping("/{projectId}")
    @RequiresFinPermission(FinPermission.MANAGE_USERS)
    public ResponseEntity<ApiResponse<Void>> deleteProject(
            @RequestHeader("X-Organization-Id") String organizationId,
            @PathVariable String projectId) {
        projectService.deleteProject(organizationId, projectId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
