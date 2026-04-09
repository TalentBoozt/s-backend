package com.talentboozt.s_backend.domains.leads.controller;

import com.talentboozt.s_backend.domains.audit_logs.model.LeadOSAuditLog;
import com.talentboozt.s_backend.domains.audit_logs.repository.mongodb.LeadOSAuditLogRepository;
import com.talentboozt.s_backend.shared.security.utils.SecurityUtils;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/leads/audit-logs")
public class LAuditLogController {
    private final LeadOSAuditLogRepository repository;
    private final SecurityUtils securityUtils;

    public LAuditLogController(LeadOSAuditLogRepository repository, SecurityUtils securityUtils) {
        this.repository = repository;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    public List<LeadOSAuditLog> getAuditLogs() {
        String wsId = securityUtils.getCurrentWorkspaceId();
        if (wsId == null) throw new RuntimeException("No workspace found");
        return repository.findByWorkspaceIdOrderByTimestampDesc(wsId);
    }
}
