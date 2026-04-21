package com.talentboozt.s_backend.domains.edu.controller;

import com.talentboozt.s_backend.domains.edu.model.EUser;
import com.talentboozt.s_backend.domains.edu.model.EAuditLog;
import com.talentboozt.s_backend.domains.edu.model.ESystemSettings;
import com.talentboozt.s_backend.domains.edu.service.EduAdminService;
import com.talentboozt.s_backend.domains.edu.service.EduAuditService;
import com.talentboozt.s_backend.domains.edu.service.EduSettingsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import com.talentboozt.s_backend.domains.edu.enums.ERoles;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/edu/admin")
public class EduAdminController {

    private final EduAdminService adminService;
    private final EduAuditService auditService;
    private final EduSettingsService settingsService;

    public EduAdminController(EduAdminService adminService, EduAuditService auditService, EduSettingsService settingsService) {
        this.adminService = adminService;
        this.auditService = auditService;
        this.settingsService = settingsService;
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('PLATFORM_ADMIN')")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(adminService.getGlobalStats());
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('PLATFORM_ADMIN')")
    public ResponseEntity<Page<EUser>> getUsers(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(adminService.getUsers(search, page, size));
    }

    @PutMapping("/users/{userId}/status")
    @PreAuthorize("hasAuthority('PLATFORM_ADMIN')")
    public ResponseEntity<Void> updateUserStatus(
            @PathVariable String userId,
            @RequestBody Map<String, Object> body,
            HttpServletRequest request) {
        adminService.updateUserStatus(
                userId,
                (Boolean) body.get("isBanned"),
                (Boolean) body.get("isActive"),
                (String) body.get("banReason")
        );
        
        auditService.logAction(
            SecurityContextHolder.getContext().getAuthentication().getName(),
            "ADMIN_UPDATE_USER_STATUS",
            userId,
            "USER",
            null,
            body,
            request
        );

        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{userId}/roles")
    @PreAuthorize("hasAuthority('PLATFORM_ADMIN')")
    public ResponseEntity<Void> updateUserRoles(
            @PathVariable String userId,
            @RequestBody Map<String, Object> body,
            HttpServletRequest request) {
        Object rolesObj = body.get("roles");
        if (rolesObj instanceof java.util.List) {
            java.util.List<?> rolesList = (java.util.List<?>) rolesObj;
            ERoles[] roles = rolesList.stream()
                .map(r -> ERoles.valueOf(r.toString()))
                .toArray(ERoles[]::new);
            adminService.updateUserRoles(userId, roles);

            auditService.logAction(
                SecurityContextHolder.getContext().getAuthentication().getName(),
                "ADMIN_UPDATE_USER_ROLES",
                userId,
                "USER",
                null,
                body,
                request
            );
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users/invite")
    @PreAuthorize("hasAuthority('PLATFORM_ADMIN')")
    public ResponseEntity<EUser> inviteUser(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        String email = (String) body.get("email");
        String firstName = (String) body.get("firstName");
        String lastName = (String) body.get("lastName");
        
        Object rolesObj = body.get("roles");
        ERoles[] roles = null;
        if (rolesObj instanceof java.util.List) {
            java.util.List<?> rolesList = (java.util.List<?>) rolesObj;
            roles = rolesList.stream()
                .map(r -> ERoles.valueOf(r.toString()))
                .toArray(ERoles[]::new);
        }

        EUser savedUser = adminService.inviteUser(email, firstName, lastName, roles);

        auditService.logAction(
            SecurityContextHolder.getContext().getAuthentication().getName(),
            "ADMIN_INVITE_USER",
            savedUser.getId(),
            "USER",
            null,
            Map.of("email", email, "roles", roles != null ? roles : "DEFAULT"),
            request
        );

        return ResponseEntity.ok(savedUser);
    }

    @GetMapping("/settings")
    @PreAuthorize("hasAuthority('PLATFORM_ADMIN')")
    public ResponseEntity<List<ESystemSettings>> getAllSettings() {
        return ResponseEntity.ok(settingsService.getAllSettings());
    }

    @GetMapping("/settings/{category}")
    @PreAuthorize("hasAuthority('PLATFORM_ADMIN')")
    public ResponseEntity<ESystemSettings> getSettingsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(settingsService.getSettingsByCategory(category));
    }

    @PutMapping("/settings/{category}")
    @PreAuthorize("hasAuthority('PLATFORM_ADMIN')")
    public ResponseEntity<ESystemSettings> updateSettings(
            @PathVariable String category,
            @RequestBody Map<String, Object> body,
            HttpServletRequest request) {
        
        String adminId = SecurityContextHolder.getContext().getAuthentication().getName();
        ESystemSettings updated = settingsService.updateSettings(category, body, adminId);

        auditService.logAction(
            adminId,
            "UPDATE_SYSTEM_SETTINGS",
            category,
            "SYSTEM_SETTINGS",
            null,
            body,
            request
        );

        return ResponseEntity.ok(updated);
    }

    @GetMapping("/audit-logs")
    @PreAuthorize("hasAuthority('PLATFORM_ADMIN')")
    public ResponseEntity<Page<EAuditLog>> getAuditLogs(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(auditService.getAuditLogs(search, page, size));
    }
}
