package com.talentboozt.s_backend.Service.common.auth;

import com.talentboozt.s_backend.Model.common.auth.PermissionModel;
import com.talentboozt.s_backend.Model.common.auth.RoleModel;
import com.talentboozt.s_backend.Repository.common.auth.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleModel addRole(RoleModel role) {
        return roleRepository.save(role);
    }

    public Optional<RoleModel> getRoleByName(String roleName) {
        return roleRepository.findByName(roleName);
    }

    public List<PermissionModel> getPermissionsForRole(String roleName) {
        Optional<RoleModel> role = getRoleByName(roleName);
        return role.map(RoleModel::getPermissions).orElse(null);
    }
}
