package com.talentboozt.s_backend.domains.finance_planning.controllers;

import com.talentboozt.s_backend.domains.finance_planning.models.FinWorkspace;
import com.talentboozt.s_backend.domains.finance_planning.services.FinWorkspaceService;
import com.talentboozt.s_backend.domains.auth.service.CredentialsService;
import com.talentboozt.s_backend.domains.auth.model.CredentialsModel;
import com.talentboozt.s_backend.domains.finance_planning.models.FinProject;
import com.talentboozt.s_backend.domains.finance_planning.services.FinProjectService;
import com.talentboozt.s_backend.shared.dto.ApiResponse;
import com.talentboozt.s_backend.shared.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/finance/workspaces")
@RequiredArgsConstructor
public class FinWorkspaceController {
    private final FinWorkspaceService workspaceService;
    private final FinProjectService projectService;
    private final CredentialsService credentialsService;
    private final SecurityUtils securityUtils;

    @PostMapping
    public ResponseEntity<ApiResponse<FinWorkspace>> createWorkspace(@RequestBody Map<String, String> payload) {
        String userId = securityUtils.getCurrentUserId();
        String name = payload.get("name");
        
        FinWorkspace workspace = workspaceService.createWorkspace(name, userId);
        
        // Update user credentials to include this workspace
        credentialsService.getCredentials(userId).ifPresent(credentials -> {
            List<Map<String, String>> orgs = credentials.getOrganizations();
            if (orgs == null) orgs = new ArrayList<>();
            
            Map<String, String> org = new HashMap<>();
            org.put("id", workspace.getId());
            org.put("name", workspace.getName());
            org.put("role", "OWNER");
            orgs.add(org);
            
            credentials.setOrganizations(orgs);
            credentials.setActiveWorkspaceId(workspace.getId());
            
            // Add to accessed platforms if not present
            List<String> platforms = credentials.getAccessedPlatforms();
            if (platforms == null) platforms = new ArrayList<>();
            if (!platforms.contains("SalesFlow")) {
                platforms.add("SalesFlow");
                credentials.setAccessedPlatforms(platforms);
            }
            
            credentialsService.updateCredentials(userId, credentials);
        });
        
        // Create Default Project
        FinProject defaultProject = new FinProject();
        defaultProject.setName("Default Sales Plan");
        defaultProject.setDescription("Automatically created default project for your workspace.");
        defaultProject.setOrganizationId(workspace.getId());
        defaultProject.setType("SAAS");
        projectService.createProject(defaultProject);
        
        return ResponseEntity.ok(ApiResponse.success(workspace));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<FinWorkspace>>> getMyWorkspaces() {
        String userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(workspaceService.getUserWorkspaces(userId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FinWorkspace>> getWorkspace(@PathVariable String id) {
        return workspaceService.getWorkspaceById(id)
                .map(ws -> ResponseEntity.ok(ApiResponse.success(ws)))
                .orElse(ResponseEntity.notFound().build());
    }
}
