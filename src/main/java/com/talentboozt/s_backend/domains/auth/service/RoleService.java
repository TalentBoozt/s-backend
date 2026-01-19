package com.talentboozt.s_backend.domains.auth.service;

import com.talentboozt.s_backend.domains.auth.model.RoleModel;
import com.talentboozt.s_backend.domains.auth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
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
        RoleModel existing = roleRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new RuntimeException("Role not found"));

        existing.setName(updatedRole.getName());
        existing.setDescription(updatedRole.getDescription());
        return roleRepository.save(existing);
    }

    public void deleteRole(String id) {
        if (!roleRepository.existsById(Objects.requireNonNull(id))) {
            throw new RuntimeException("Role not found");
        }
        roleRepository.deleteById(id);
    }

    public List<String> getPermissionsByRole(String id) {
        Optional<RoleModel> role = roleRepository.findById(Objects.requireNonNull(id));
        return role.map(RoleModel::getPermissions).orElse(null);
    }

    public void updateRolePermissions(String id, List<String> permissions) {
        Optional<RoleModel> role = roleRepository.findById(Objects.requireNonNull(id));
        if (role.isPresent()) {
            RoleModel roleModel = role.get();
            roleModel.setPermissions(permissions);
            roleRepository.save(roleModel);
        }
    }
}
