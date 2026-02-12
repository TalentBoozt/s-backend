package com.talentboozt.s_backend.domains.audit_logs.controller;

import com.talentboozt.s_backend.domains.audit_logs.model.ClientActAuditLog;
import com.talentboozt.s_backend.domains.audit_logs.repository.mongodb.ClientActAuditLogRepository;

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
@RequestMapping("/api/monitoring/client-act-audit-log")
public class ClientActAuditLogController {

    @Autowired
    ClientActAuditLogRepository clientActAuditLogRepository;

    @GetMapping("/all")
    public Iterable<ClientActAuditLog> getAllLogs() {
        return clientActAuditLogRepository.findAll();
    }

    @GetMapping("/client")
    public Map<String, Object> getClientLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String filter
    ) {
        if (page < 1) page = 1;
        if (size < 1) size = 10;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<ClientActAuditLog> logPage = clientActAuditLogRepository.searchWithFilter(filter, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("items", logPage.getContent());
        response.put("total", logPage.getTotalElements());
        return response;
    }
}
