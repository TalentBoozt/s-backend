package com.talentboozt.s_backend.domains.finance_planning.services;

import com.talentboozt.s_backend.domains.finance_planning.security.rbac.FinPermission;
import com.talentboozt.s_backend.domains.finance_planning.security.rbac.FinRolePermissions;
import com.talentboozt.s_backend.domains.finance_planning.security.rbac.ProjectRole;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.FinProjectMemberRepository;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.FinProjectRepository;
import com.talentboozt.s_backend.shared.security.model.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FinPermissionService {
    private final FinProjectRepository projectRepository;
    private final FinProjectMemberRepository memberRepository;

    public boolean hasPermission(CustomUserDetails userDetails, FinPermission requiredPermission, String organizationId, String projectId) {
        String userId = userDetails.getUserId();
        if (userId == null) return false;

        // 1. Project-level check
        if (projectId != null && !projectId.isEmpty()) {
            var project = projectRepository.findById(projectId).orElse(null);
            if (project == null) return false;

            // Scenario Locking: If project is APPROVED, deny WRITE actions
            if ("APPROVED".equalsIgnoreCase(project.getStatus()) && isWritePermission(requiredPermission)) {
                // Allow only platform admins to override locked projects
                if (userDetails.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_PLATFORM_ADMIN"))) {
                    return false;
                }
            }

            if (userId.equals(project.getOwnerId())) return true;

            boolean hasProjectPermission = memberRepository.findByProjectIdAndUserId(projectId, userId)
                    .map(member -> FinRolePermissions.hasPermission(member.getRole(), requiredPermission))
                    .orElse(false);
            
            if (hasProjectPermission) return true;
        }

        // 2. Organization-level or Global actions
        if (requiredPermission == FinPermission.READ_PROJECT) {
            return true; 
        }

        // 3. Platform Admin bypass
        return userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PLATFORM_ADMIN"));
    }

    private boolean isWritePermission(FinPermission permission) {
        return permission == FinPermission.WRITE_PROJECT ||
               permission == FinPermission.EDIT_FINANCIALS ||
               permission == FinPermission.MANAGE_SCENARIOS ||
               permission == FinPermission.MANAGE_USERS;
    }
}
