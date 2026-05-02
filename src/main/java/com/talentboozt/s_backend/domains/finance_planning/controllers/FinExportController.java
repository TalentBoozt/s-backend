package com.talentboozt.s_backend.domains.finance_planning.controllers;

import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.FinAssumptionRepository;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.FinSalesPlanRepository;
import com.talentboozt.s_backend.domains.finance_planning.security.annotations.RequiresFinPermission;
import com.talentboozt.s_backend.domains.finance_planning.security.rbac.FinPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/finance/export")
@RequiredArgsConstructor
public class FinExportController {
    private final FinAssumptionRepository assumptionRepository;
    private final FinSalesPlanRepository salesRepository;

    @GetMapping("/{projectId}/csv")
    @RequiresFinPermission(value = FinPermission.READ_PROJECT, orgIdSource = "header", projectIdSource = "path")
    public ResponseEntity<byte[]> exportCsv(
            @RequestHeader("X-Organization-Id") String organizationId,
            @PathVariable String projectId) {
        
        StringBuilder csv = new StringBuilder("Type,Name,Value,Month\n");
        
        // Export Assumptions
        assumptionRepository.findByOrganizationIdAndProjectId(organizationId, projectId).forEach(a -> 
            csv.append("Assumption,").append(a.getKey()).append(",").append(a.getValue()).append(",N/A\n")
        );
        
        // Export Sales Plan
        salesRepository.findByOrganizationIdAndProjectId(organizationId, projectId).forEach(s -> {
            if (s.getUserCounts() != null) {
                s.getUserCounts().forEach((tier, count) -> 
                    csv.append("Sales,").append(tier).append(",").append(count).append(",").append(s.getMonth()).append("\n")
                );
            }
        });
        
        byte[] content = csv.toString().getBytes();
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=project_export_" + projectId + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(content);
    }
}
