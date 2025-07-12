package com.talentboozt.s_backend.domains.auth.service;

import com.talentboozt.s_backend.domains.auth.dto.PermissionRequest;
import com.talentboozt.s_backend.domains.auth.model.PermissionModel;

import java.util.List;

public interface PermissionService {
    List<PermissionModel> getAllPermissions();
    PermissionModel createPermission(PermissionRequest request);
    PermissionModel updatePermission(String id, PermissionRequest request);
    void deletePermission(String id);
}

