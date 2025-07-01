package com.talentboozt.s_backend.Controller.SYS_TRACKING;

import com.talentboozt.s_backend.Model.SYS_TRACKING.StripeAuditLog;
import com.talentboozt.s_backend.Repository.SYS_TRACKING.StripeAuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
