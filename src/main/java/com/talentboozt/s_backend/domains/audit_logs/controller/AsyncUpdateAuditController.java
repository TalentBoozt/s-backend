package com.talentboozt.s_backend.domains.audit_logs.controller;

import com.talentboozt.s_backend.domains.audit_logs.model.AsyncUpdateAuditLog;
import com.talentboozt.s_backend.domains.audit_logs.repository.AsyncUpdateAuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/monitoring/async-audit-log")
public class AsyncUpdateAuditController {

    @Autowired
    private AsyncUpdateAuditLogRepository asyncUpdateAuditLogRepository;

    @GetMapping("/all")
    public Iterable<AsyncUpdateAuditLog> getAllLogs() {
        return asyncUpdateAuditLogRepository.findAll();
    }

    @GetMapping("/count")
    public long getLogCount() {
        return asyncUpdateAuditLogRepository.count();
    }

    @GetMapping("/latest")
    public AsyncUpdateAuditLog getLatestLog() {
        return asyncUpdateAuditLogRepository.findTopByOrderByCreatedAtDesc();
    }

    @GetMapping("/paginated")
    public Map<String, Object> getPaginatedLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String filter
    ) {
        if (page < 1) page = 1;
        if (size < 1) size = 10;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AsyncUpdateAuditLog> logPage = asyncUpdateAuditLogRepository.searchWithFilter(filter, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("items", logPage.getContent());
        response.put("total", logPage.getTotalElements());
        return response;
    }
}
