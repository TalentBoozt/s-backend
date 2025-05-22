package com.talentboozt.s_backend.Service.common.auth;

import com.talentboozt.s_backend.DTO.common.auth.PermissionRequest;
import com.talentboozt.s_backend.Model.common.auth.PermissionModel;

import java.util.List;

public interface PermissionService {
    List<PermissionModel> getAllPermissions();
    PermissionModel createPermission(PermissionRequest request);
    PermissionModel updatePermission(String id, PermissionRequest request);
    void deletePermission(String id);
}

