package com.talentboozt.s_backend.domains.finance_planning.services;

import com.talentboozt.s_backend.domains.finance_planning.security.rbac.FinPermission;
import com.talentboozt.s_backend.shared.security.model.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("finSecurity")
@RequiredArgsConstructor
public class FinSecurityService {
    private final FinPermissionService permissionService;

    public boolean hasPermission(String permissionName, String organizationId) {
        return hasPermission(permissionName, organizationId, null);
    }

    public boolean hasPermission(String permissionName, String organizationId, String projectId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails userDetails)) {
            return false;
        }

        try {
            FinPermission permission = FinPermission.valueOf(permissionName);
            return permissionService.hasPermission(userDetails, permission, organizationId, projectId);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean canAccessProject(String organizationId, String projectId) {
        return hasPermission("READ_PROJECT", organizationId, projectId);
    }

    public boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }
}
