package com.talentboozt.s_backend.domains.admin.service;

import com.talentboozt.s_backend.domains.admin.dto.PagedResponseDTO;
import com.talentboozt.s_backend.domains.admin.dto.StatusUpdateDTO;
import com.talentboozt.s_backend.domains.admin.dto.UserDetailDTO;
import com.talentboozt.s_backend.domains.admin.dto.UserManagementDTO;

import java.util.List;

public interface  UserManagementService {
    PagedResponseDTO<UserManagementDTO> getUsers(String search, String role,
                                                 String platform, Boolean active,
                                                 int page, int size);

    UserDetailDTO getUserDetails(String userId);

    void updateStatus(String userId, StatusUpdateDTO status);

    void updateRoles(String userId, List<String> roles);

    void updatePermissions(String userId, List<String> permissions);
}
