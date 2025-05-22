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

    public Optional<RoleModel> getRoleByName(String roleName) {
        return roleRepository.findByName(roleName);
    }

    public List<String> getPermissionsForRole(String roleName) {
        Optional<RoleModel> role = getRoleByName(roleName);
        return role.map(RoleModel::getPermissions).orElse(null);
    }

    public List<RoleModel> getAllRoles() {
        return roleRepository.findAll();
    }

    public RoleModel addRole(RoleModel role) {
        if (roleRepository.existsByName(role.getName())) {
            throw new RuntimeException("Role with this name already exists.");
        }
        return roleRepository.save(role);
    }

    public RoleModel updateRole(String id, RoleModel updatedRole) {
        RoleModel existing = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        existing.setName(updatedRole.getName());
        existing.setDescription(updatedRole.getDescription());
        return roleRepository.save(existing);
    }

    public void deleteRole(String id) {
        if (!roleRepository.existsById(id)) {
            throw new RuntimeException("Role not found");
        }
        roleRepository.deleteById(id);
    }

    public List<String> getPermissionsByRole(String id) {
        Optional<RoleModel> role = roleRepository.findById(id);
        return role.map(RoleModel::getPermissions).orElse(null);
    }

    public void updateRolePermissions(String id, List<String> permissions) {
        Optional<RoleModel> role = roleRepository.findById(id);
        if (role.isPresent()) {
            RoleModel roleModel = role.get();
            roleModel.setPermissions(permissions);
            roleRepository.save(roleModel);
        }
    }
}
