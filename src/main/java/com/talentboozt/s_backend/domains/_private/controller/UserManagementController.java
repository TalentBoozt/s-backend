package com.talentboozt.s_backend.domains._private.controller;

import com.talentboozt.s_backend.domains._private.dto.*;
import com.talentboozt.s_backend.domains._private.service.UserManagementService;
import com.talentboozt.s_backend.domains.auth.model.CredentialsModel;
import com.talentboozt.s_backend.domains.auth.repository.CredentialsRepository;
import com.talentboozt.s_backend.domains.common.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class UserManagementController {

    @Autowired
    private UserManagementService userService;

    @Autowired
    private CredentialsRepository credentialsRepository;

    @GetMapping
    public PagedResponseDTO<UserManagementDTO> getUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String platform,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {

        return userService.getUsers(search, role, platform, active, page, size);
    }

    @GetMapping("/{userId}")
    public UserDetailDTO getUserDetails(@PathVariable String userId) {
        return userService.getUserDetails(userId);
    }

    @PostMapping("/{userId}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable String userId, @RequestBody StatusUpdateDTO dto) {
        userService.updateStatus(userId, dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/roles")
    public ResponseEntity<Void> updateRoles(@PathVariable String userId, @RequestBody RoleUpdateDTO dto) {
        userService.updateRoles(userId, dto.getRoles());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/permissions")
    public ResponseEntity<Void> updatePermissions(@PathVariable String userId, @RequestBody PermissionUpdateDTO dto) {
        userService.updatePermissions(userId, dto.getPermissions());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/bulk-status")
    public ResponseEntity<?> bulkUpdateUserStatus(@RequestBody BulkStatusUpdateRequest request) {
        List<CredentialsModel> usersToUpdate;

        if (Boolean.TRUE.equals(request.getOnlyFilteredUsers())) {
            usersToUpdate = credentialsRepository.findUsersByFilters(
                    request.getSearch(),
                    request.getRole(),
                    request.getPlatform(),
                    request.getFilterActive()
            );
        } else {
            usersToUpdate = credentialsRepository.findAllByEmployeeIdIn(request.getUserIds());
        }

        for (CredentialsModel user : usersToUpdate) {
            if (request.getActive() != null) user.setActive(request.getActive());
            if (request.getDisabled() != null) user.setDisabled(request.getDisabled());
            if (request.getAmbassador() != null) user.setAmbassador(request.getAmbassador());
        }

        credentialsRepository.saveAll(usersToUpdate);
        return ResponseEntity.ok(new ApiResponse("Updated " + usersToUpdate.size() + " users"));
    }

}
