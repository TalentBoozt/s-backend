package com.talentboozt.s_backend.Service.common.auth;

import com.talentboozt.s_backend.Model.common.auth.PermissionModel;
import com.talentboozt.s_backend.Repository.common.auth.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionModel getPermissionByName(String name) {
        return permissionRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Permission not found: " + name));
    }

    public List<PermissionModel> getAllPermissions() {
        return permissionRepository.findAll();
    }
}

