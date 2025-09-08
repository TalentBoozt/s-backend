package com.talentboozt.s_backend.domains.auth.service;

import com.talentboozt.s_backend.domains.auth.model.RoleModel;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserPermissionsService {

    private final RoleService roleService;

    public UserPermissionsService(RoleService roleService) {
        this.roleService = roleService;
    }

    public List<String> resolvePermissions(List<String> roles) {
        Set<String> permissions = new HashSet<>();
        for (String roleName : roles) {
            Optional<RoleModel> roleOpt = roleService.getRoleByName(roleName);
            roleOpt.ifPresent(role -> {
                if (role.getPermissions() != null) {
                    permissions.addAll(role.getPermissions());
                }
            });
        }
        return new ArrayList<>(permissions);
    }
}
