package com.talentboozt.s_backend.domains.finance_planning.controllers;

import com.talentboozt.s_backend.domains.finance_planning.dtos.BulkUpdateDto;
import com.talentboozt.s_backend.domains.finance_planning.dtos.FinanceStateDto;
import com.talentboozt.s_backend.domains.finance_planning.services.FinFinanceStateService;
import com.talentboozt.s_backend.domains.finance_planning.services.FinBulkUpdateService;
import com.talentboozt.s_backend.domains.finance_planning.security.annotations.RequiresFinPermission;
import com.talentboozt.s_backend.domains.finance_planning.security.rbac.FinPermission;
import com.talentboozt.s_backend.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import com.talentboozt.s_backend.domains.finance_planning.models.FinBudget;
import com.talentboozt.s_backend.domains.finance_planning.models.FinSalesPlan;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/finance")
@RequiredArgsConstructor
public class FinFinanceController {
    private final FinFinanceStateService financeStateService;
    private final FinBulkUpdateService bulkUpdateService;

    @GetMapping("/projects/{projectId}/full-state")
    @RequiresFinPermission(value = FinPermission.READ_PROJECT, orgIdSource = "header", projectIdSource = "path")
    public ResponseEntity<ApiResponse<FinanceStateDto>> getFullState(
            @RequestHeader("X-Organization-Id") String organizationId,
            @PathVariable String projectId) {
        FinanceStateDto state = financeStateService.getFullState(organizationId, projectId);
        return ResponseEntity.ok(ApiResponse.success(state));
    }

    @GetMapping("/projects/{projectId}/sales")
    @RequiresFinPermission(value = FinPermission.READ_PROJECT, orgIdSource = "header", projectIdSource = "path")
    public ResponseEntity<ApiResponse<Page<FinSalesPlan>>> getPaginatedSales(
            @RequestHeader("X-Organization-Id") String organizationId,
            @PathVariable String projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<FinSalesPlan> sales = financeStateService.getPaginatedSales(organizationId, projectId, page, size);
        return ResponseEntity.ok(ApiResponse.success(sales));
    }

    @GetMapping("/projects/{projectId}/budget")
    @RequiresFinPermission(value = FinPermission.READ_PROJECT, orgIdSource = "header", projectIdSource = "path")
    public ResponseEntity<ApiResponse<Page<FinBudget>>> getPaginatedBudget(
            @RequestHeader("X-Organization-Id") String organizationId,
            @PathVariable String projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<FinBudget> budget = financeStateService.getPaginatedBudget(organizationId, projectId, page, size);
        return ResponseEntity.ok(ApiResponse.success(budget));
    }

    @PostMapping("/projects/{projectId}/bulk-update")
    @RequiresFinPermission(value = FinPermission.EDIT_FINANCIALS, orgIdSource = "header", projectIdSource = "path")
    public ResponseEntity<ApiResponse<Void>> bulkUpdate(
            @RequestHeader("X-Organization-Id") String organizationId,
            @PathVariable String projectId,
            @RequestBody BulkUpdateDto request) {
        bulkUpdateService.processBulkUpdate(organizationId, projectId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/projects/{projectId}/draft")
    @RequiresFinPermission(value = FinPermission.EDIT_FINANCIALS, orgIdSource = "header", projectIdSource = "path")
    public ResponseEntity<ApiResponse<Void>> saveDraft(
            @RequestHeader("X-Organization-Id") String organizationId,
            @PathVariable String projectId,
            @RequestBody FinanceStateDto state) {
        financeStateService.saveFullState(organizationId, projectId, state);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
