package com.talentboozt.s_backend.domains.finance_planning.controllers;

import com.talentboozt.s_backend.domains.finance_planning.dtos.BulkUpdateDto;
import com.talentboozt.s_backend.domains.finance_planning.dtos.FinanceStateDto;
import com.talentboozt.s_backend.domains.finance_planning.services.FinFinanceStateService;
import com.talentboozt.s_backend.domains.finance_planning.services.FinBulkUpdateService; // We will create this
import com.talentboozt.s_backend.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/finance")
@RequiredArgsConstructor
public class FinFinanceController {
    private final FinFinanceStateService financeStateService;
    private final FinBulkUpdateService bulkUpdateService;

    @GetMapping("/projects/{projectId}/full-state")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE_MANAGER') or hasPermission(#projectId, 'READ')")
    public ResponseEntity<ApiResponse<FinanceStateDto>> getFullState(
            @RequestHeader("X-Organization-Id") String organizationId,
            @PathVariable String projectId) {
        FinanceStateDto state = financeStateService.getFullState(organizationId, projectId);
        return ResponseEntity.ok(ApiResponse.success(state));
    }

    @PostMapping("/projects/{projectId}/bulk-update")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE_MANAGER') or hasPermission(#projectId, 'WRITE')")
    public ResponseEntity<ApiResponse<Void>> bulkUpdate(
            @RequestHeader("X-Organization-Id") String organizationId,
            @PathVariable String projectId,
            @RequestBody BulkUpdateDto request) {
        bulkUpdateService.processBulkUpdate(organizationId, projectId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
