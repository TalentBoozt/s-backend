package com.talentboozt.s_backend.domains.edu.controller;

import com.talentboozt.s_backend.domains.edu.model.EWorkspaces;
import com.talentboozt.s_backend.domains.edu.service.EduAdminService;
import com.talentboozt.s_backend.shared.security.annotations.AuthenticatedUser;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/edu/admin/workspaces")
public class EduAdminWorkspaceController {

    private final EduAdminService adminService;

    public EduAdminWorkspaceController(EduAdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PLATFORM_ADMIN')")
    public ResponseEntity<Page<EWorkspaces>> getWorkspaces(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(adminService.getWorkspaces(search, page, size));
    }

    @PutMapping("/{workspaceId}/status")
    @PreAuthorize("hasAuthority('PLATFORM_ADMIN')")
    public ResponseEntity<Void> updateWorkspaceStatus(
            @PathVariable String workspaceId,
            @RequestBody Map<String, Object> body) {
        adminService.updateWorkspaceStatus(
                workspaceId,
                (Boolean) body.get("isActive")
        );
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{workspaceId}/tier")
    @PreAuthorize("hasAuthority('PLATFORM_ADMIN')")
    public ResponseEntity<Void> updateWorkspaceTier(
            @PathVariable String workspaceId,
            @RequestBody Map<String, Object> body) {
        adminService.updateWorkspaceTier(
                workspaceId,
                (String) body.get("plan")
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{workspaceId}/verify")
    @PreAuthorize("hasAuthority('PLATFORM_ADMIN')")
    public ResponseEntity<Void> verifyWorkspace(@PathVariable String workspaceId) {
        adminService.verifyWorkspace(workspaceId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{workspaceId}/compliance")
    @PreAuthorize("hasAuthority('PLATFORM_ADMIN')")
    public ResponseEntity<Map<String, Object>> getWorkspaceCompliance(@PathVariable String workspaceId) {
        return ResponseEntity.ok(adminService.getWorkspaceCompliance(workspaceId));
    }
}
