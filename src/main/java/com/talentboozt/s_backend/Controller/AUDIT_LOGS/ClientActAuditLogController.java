package com.talentboozt.s_backend.Controller.AUDIT_LOGS;

import com.talentboozt.s_backend.Model.AUDIT_LOGS.ClientActAuditLog;
import com.talentboozt.s_backend.Repository.AUDIT_LOGS.ClientActAuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClientActAuditLogController {

    @Autowired
    ClientActAuditLogRepository clientActAuditLogRepository;

    @GetMapping("/all")
    public Iterable<ClientActAuditLog> getAllLogs() {
        return clientActAuditLogRepository.findAll();
    }
}
