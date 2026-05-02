package com.talentboozt.s_backend.domains.finance_planning.plat_admin.controller;

import com.talentboozt.s_backend.domains.workspace.model.WorkspaceModel;
import com.talentboozt.s_backend.domains.workspace.service.WorkspaceService;
import com.talentboozt.s_backend.domains.finance_planning.models.FinWorkspace;
import com.talentboozt.s_backend.domains.finance_planning.services.FinWorkspaceService;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.FinProjectRepository;
import com.talentboozt.s_backend.domains.finance_planning.models.FinAiTrainingSnapshot;
import com.talentboozt.s_backend.domains.finance_planning.models.FinAuditLog;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.FinAiTrainingSnapshotRepository;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.FinAuditLogRepository;
import com.talentboozt.s_backend.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/platform-admin")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'STAFF')")
public class PlatformAdminController {
    private final WorkspaceService workspaceService;
    private final FinWorkspaceService finWorkspaceService;
    private final FinProjectRepository projectRepository;
    private final FinAuditLogRepository auditLogRepository;
    private final FinAiTrainingSnapshotRepository aiTrainingRepository;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPlatformStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalEduOrganizations", workspaceService.getAllWorkspaces().size());
        stats.put("totalFinWorkspaces", finWorkspaceService.getAllWorkspaces().size());
        stats.put("totalProjects", projectRepository.count());
        stats.put("totalAuditLogs", auditLogRepository.count());
        stats.put("totalAiTrainingSnapshots", aiTrainingRepository.count());
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/ai-training")
    public ResponseEntity<ApiResponse<Page<FinAiTrainingSnapshot>>> getAiTrainingData(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Page<FinAiTrainingSnapshot> data = aiTrainingRepository
                .findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/organizations")
    public ResponseEntity<ApiResponse<List<FinWorkspace>>> getAllOrganizations() {
        return ResponseEntity.ok(ApiResponse.success(finWorkspaceService.getAllWorkspaces()));
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<ApiResponse<Page<FinAuditLog>>> getGlobalAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Page<FinAuditLog> logs = auditLogRepository
                .findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp")));
        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    @PostMapping("/organizations")
    public ResponseEntity<ApiResponse<FinWorkspace>> createOrganization(@RequestBody Map<String, String> payload) {
        String name = payload.get("name");
        String ownerId = payload.get("ownerId");
        return ResponseEntity.ok(ApiResponse.success(finWorkspaceService.createWorkspace(name, ownerId)));
    }

    @DeleteMapping("/organizations/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOrganization(@PathVariable String id) {
        finWorkspaceService.deleteWorkspace(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/ai-training/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAiSnapshot(@PathVariable String id) {
        aiTrainingRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
