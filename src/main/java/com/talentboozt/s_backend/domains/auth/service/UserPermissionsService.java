package com.talentboozt.s_backend.domains.auth.service;

import com.talentboozt.s_backend.domains.auth.model.RoleModel;
import com.talentboozt.s_backend.domains.auth.constant.PermissionConstants;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserPermissionsService {

    private final RoleService roleService;

    public UserPermissionsService(RoleService roleService) {
        this.roleService = roleService;
    }

    public List<String> resolvePermissions(List<String> roles) {
        if (roles == null || roles.isEmpty())
            return new ArrayList<>();
        Set<String> permissions = new HashSet<>();
        Set<String> visitedRoles = new HashSet<>();
        Queue<String> roleQueue = new LinkedList<>(roles);

        while (!roleQueue.isEmpty()) {
            String roleName = roleQueue.poll();
            if (visitedRoles.contains(roleName))
                continue;
            visitedRoles.add(roleName);

            Optional<RoleModel> roleOpt = roleService.getRoleByName(roleName);
            roleOpt.ifPresent(role -> {
                if (role.getPermissions() != null) {
                    permissions.addAll(role.getPermissions());
                }
                if (role.getInheritsFrom() != null) {
                    roleQueue.addAll(role.getInheritsFrom());
                }
            });
        }
        return new ArrayList<>(permissions);
    }

    public List<String> resolvePermissionsWithSystemBypass(List<String> roles, boolean isSystemOwner) {
        List<String> permissions = resolvePermissions(roles);
        if (isSystemOwner) {
            if (!permissions.contains(PermissionConstants.SYSTEM_BYPASS_BILLING)) {
                permissions.add(PermissionConstants.SYSTEM_BYPASS_BILLING);
            }
            if (!permissions.contains(PermissionConstants.SYSTEM_MANAGE)) {
                permissions.add(PermissionConstants.SYSTEM_MANAGE);
            }
        }
        return permissions;
    }
}
