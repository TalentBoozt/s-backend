package com.talentboozt.s_backend.domains.finance_planning.controllers;

import com.talentboozt.s_backend.domains.auth.model.CredentialsModel;
import com.talentboozt.s_backend.domains.auth.service.CredentialsService;
import com.talentboozt.s_backend.domains.finance_planning.models.FinWorkspace;
import com.talentboozt.s_backend.domains.finance_planning.services.FinWorkspaceService;
import com.talentboozt.s_backend.domains.user.model.EmployeeModel;
import com.talentboozt.s_backend.domains.user.service.EmployeeService;
import com.talentboozt.s_backend.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/finance/workspaces/{workspaceId}/team")
@RequiredArgsConstructor
public class FinTeamController {
    private final CredentialsService credentialsService;
    private final EmployeeService employeeService;
    private final FinWorkspaceService workspaceService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getTeamMembers(@PathVariable String workspaceId) {
        List<CredentialsModel> teamCredentials = credentialsService.getCredentialsByOrganizationId(workspaceId);
        List<Map<String, Object>> members = teamCredentials.stream()
                .map(c -> {
                    Map<String, Object> member = new HashMap<>();
                    member.put("userId", c.getEmployeeId());
                    member.put("email", c.getEmail());
                    member.put("name", c.getFirstname() + " " + c.getLastname());
                    
                    // Find role in this workspace
                    String role = c.getOrganizations().stream()
                            .filter(o -> workspaceId.equals(o.get("id")))
                            .map(o -> o.get("role"))
                            .findFirst()
                            .orElse("MEMBER");
                    member.put("role", role);
                    return member;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(members));
    }

    @PostMapping("/invite")
    public ResponseEntity<ApiResponse<Void>> inviteMember(
            @PathVariable String workspaceId,
            @RequestBody Map<String, String> payload) {
        
        String email = payload.get("email");
        String role = payload.getOrDefault("role", "MEMBER");
        
        CredentialsModel target = credentialsService.getCredentialsByEmail(email);
        if (target == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("User not found with email: " + email));
        }

        FinWorkspace workspace = workspaceService.getWorkspaceById(workspaceId)
                .orElseThrow(() -> new RuntimeException("Workspace not found"));

        List<Map<String, String>> orgs = target.getOrganizations();
        if (orgs == null) orgs = new ArrayList<>();
        
        // Check if already a member
        boolean alreadyMember = orgs.stream().anyMatch(o -> workspaceId.equals(o.get("id")));
        if (!alreadyMember) {
            Map<String, String> org = new HashMap<>();
            org.put("id", workspace.getId());
            org.put("name", workspace.getName());
            org.put("role", role);
            orgs.add(org);
            
            target.setOrganizations(orgs);
            
            // Add platform if not present
            List<String> platforms = target.getAccessedPlatforms();
            if (platforms == null) platforms = new ArrayList<>();
            if (!platforms.contains("SalesFlow")) {
                platforms.add("SalesFlow");
                target.setAccessedPlatforms(platforms);
            }
            
            credentialsService.updateCredentials(target.getEmployeeId(), target);
        }

        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @PathVariable String workspaceId,
            @PathVariable String userId) {
        
        credentialsService.getCredentials(userId).ifPresent(target -> {
            List<Map<String, String>> orgs = target.getOrganizations();
            if (orgs != null) {
                orgs.removeIf(o -> workspaceId.equals(o.get("id")));
                target.setOrganizations(orgs);
                credentialsService.updateCredentials(userId, target);
            }
        });

        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
