package com.talentboozt.s_backend.domains.finance_planning.security.rbac;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.talentboozt.s_backend.domains.finance_planning.security.rbac.FinPermission.*;

public class FinRolePermissions {
    private static final Map<OrganizationRole, Set<FinPermission>> ORG_ROLE_PERMISSIONS = new HashMap<>();
    private static final Map<ProjectRole, Set<FinPermission>> PROJECT_ROLE_PERMISSIONS = new HashMap<>();

    static {
        // Organization Roles Mapping
        ORG_ROLE_PERMISSIONS.put(OrganizationRole.FINANCE_ADMIN, EnumSet.allOf(FinPermission.class));
        
        ORG_ROLE_PERMISSIONS.put(OrganizationRole.FINANCE_PROJECT_MANAGER, EnumSet.of(
            READ_PROJECT, WRITE_PROJECT, MANAGE_SCENARIOS, VIEW_ANALYTICS, EDIT_FINANCIALS
        ));
        
        ORG_ROLE_PERMISSIONS.put(OrganizationRole.FINANCE_MEMBER, EnumSet.of(
            READ_PROJECT, VIEW_ANALYTICS, EDIT_FINANCIALS
        ));
        
        ORG_ROLE_PERMISSIONS.put(OrganizationRole.FINANCE_VIEWER, EnumSet.of(
            READ_PROJECT, VIEW_ANALYTICS
        ));

        // Project Roles Mapping (Resource level)
        PROJECT_ROLE_PERMISSIONS.put(ProjectRole.PROJECT_OWNER, EnumSet.allOf(FinPermission.class));
        
        PROJECT_ROLE_PERMISSIONS.put(ProjectRole.PROJECT_EDITOR, EnumSet.of(
            READ_PROJECT, WRITE_PROJECT, MANAGE_SCENARIOS, VIEW_ANALYTICS, EDIT_FINANCIALS
        ));
        
        PROJECT_ROLE_PERMISSIONS.put(ProjectRole.PROJECT_VIEWER, EnumSet.of(
            READ_PROJECT, VIEW_ANALYTICS
        ));
    }

    public static boolean hasPermission(OrganizationRole role, FinPermission permission) {
        return ORG_ROLE_PERMISSIONS.getOrDefault(role, EnumSet.noneOf(FinPermission.class)).contains(permission);
    }

    public static boolean hasPermission(ProjectRole role, FinPermission permission) {
        return PROJECT_ROLE_PERMISSIONS.getOrDefault(role, EnumSet.noneOf(FinPermission.class)).contains(permission);
    }
}
