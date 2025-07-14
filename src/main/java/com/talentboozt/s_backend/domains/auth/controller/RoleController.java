package com.talentboozt.s_backend.domains.auth.controller;

import com.talentboozt.s_backend.domains.auth.model.RoleModel;
import com.talentboozt.s_backend.domains.auth.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/get")
    public List<RoleModel> getAll() {
        return roleService.getAllRoles();
    }

    @GetMapping("/name/{name}")
    public RoleModel getRoleByName(@PathVariable String name) {
        return roleService.getRoleByName(name).orElse(null);
    }

    @PostMapping("/add")
    public RoleModel create(@RequestBody RoleModel role) {
        return roleService.addRole(role);
    }

    @PutMapping("/update/{id}")
    public RoleModel update(@PathVariable String id, @RequestBody RoleModel role) {
        return roleService.updateRole(id, role);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{rodeId}/permissions")
    public List<String> getPermissionsForRole(@PathVariable String rodeId) {
        return roleService.getPermissionsByRole(rodeId);
    }

    @PutMapping("/{rodeId}/update/permissions")
    public void updateRolePermissions(@PathVariable String rodeId, @RequestBody List<String> permissions) {
        roleService.updateRolePermissions(rodeId, permissions);
    }
}
