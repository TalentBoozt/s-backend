package com.talentboozt.s_backend.Controller.AUDIT_LOGS;

import com.talentboozt.s_backend.Model.AUDIT_LOGS.StripeAuditLog;
import com.talentboozt.s_backend.Repository.AUDIT_LOGS.StripeAuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitoring/stripe-audit-log")
public class StripeAuditLogController {

    @Autowired
    private StripeAuditLogRepository stripeAuditLogRepository;

    @GetMapping("/all")
    public List<StripeAuditLog> getAllLogs() {
        return stripeAuditLogRepository.findAll();
    }

    @GetMapping("/retry")
    public List<StripeAuditLog> getLogsForRetry() {
        return stripeAuditLogRepository.findTop20ByStatusAndRetryCountLessThanOrderByCreatedAtAsc("retry_pending", 3);
    }

    @GetMapping("/stripe")
    public Map<String, Object> getStripeLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String status
    ) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<StripeAuditLog> logs = stripeAuditLogRepository.search(eventType, status, pageable);

        return Map.of(
                "items", logs.getContent(),
                "total", logs.getTotalElements()
        );
    }
}
