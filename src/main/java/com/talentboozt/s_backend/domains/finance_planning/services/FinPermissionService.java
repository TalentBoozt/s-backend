package com.talentboozt.s_backend.domains.finance_planning.services;

import com.talentboozt.s_backend.domains.auth.model.CredentialsModel;
import com.talentboozt.s_backend.domains.auth.repository.mongodb.CredentialsRepository;
import com.talentboozt.s_backend.domains.finance_planning.security.rbac.*;
import com.talentboozt.s_backend.shared.security.model.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FinPermissionService {
    private final CredentialsRepository credentialsRepository;

    public boolean hasPermission(CustomUserDetails userDetails, FinPermission permission, String organizationId, String projectId) {
        if (userDetails == null) return false;

        CredentialsModel user = credentialsRepository.findById(userDetails.getUserId()).orElse(null);
        if (user == null) return false;

        // 1. PLATFORM_ADMIN check (bypass all checks)
        if (PlatformRole.PLATFORM_ADMIN.name().equals(user.getPlatformRole())) {
            return true;
        }

        // 2. STAFF check (internal support role with limited override)
        if (PlatformRole.STAFF.name().equals(user.getPlatformRole())) {
            // Staff can read projects and view analytics for support purposes
            return permission == FinPermission.READ_PROJECT || permission == FinPermission.VIEW_ANALYTICS;
        }

        // 3. Organization Role check
        OrganizationRole orgRole = getUserOrgRole(user, organizationId);
        if (orgRole != null && FinRolePermissions.hasPermission(orgRole, permission)) {
            return true;
        }

        // 4. Project Role check (if projectId is provided)
        if (projectId != null) {
            ProjectRole projectRole = getUserProjectRole(user.getId(), projectId);
            if (projectRole != null && FinRolePermissions.hasPermission(projectRole, permission)) {
                return true;
            }
        }

        return false;
    }

    private OrganizationRole getUserOrgRole(CredentialsModel user, String organizationId) {
        if (user.getOrganizations() == null) return null;
        
        return user.getOrganizations().stream()
                .filter(org -> organizationId.equals(org.get("id")) || organizationId.equals(org.get("companyId")))
                .map(org -> {
                    String roleStr = org.get("role");
                    try {
                        return roleStr != null ? OrganizationRole.valueOf(roleStr) : null;
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private ProjectRole getUserProjectRole(String userId, String projectId) {
        // TODO: Implement project-specific role lookup from a repository
        // For now, returning null to signify no project-specific role
        return null; 
    }
}
