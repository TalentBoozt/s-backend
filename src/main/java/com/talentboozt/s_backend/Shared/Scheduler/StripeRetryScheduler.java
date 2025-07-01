package com.talentboozt.s_backend.Shared.Scheduler;

import com.stripe.model.Event;
import com.talentboozt.s_backend.Controller.common.payment.StripeWebhookController;
import com.talentboozt.s_backend.Model.SYS_TRACKING.StripeAuditLog;
import com.talentboozt.s_backend.Service.SYS_TRACKING.StripeAuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StripeRetryScheduler {

    @Autowired
    private StripeAuditLogService auditLogService;

    @Autowired
    private StripeWebhookController webhookController;

    @Scheduled(fixedDelay = 60000) // Retry every 60 seconds
    public void retryFailedWebhooks() {
        List<StripeAuditLog> logs = auditLogService.getLogsForRetry(3); // 3 retries max

        for (StripeAuditLog log : logs) {
            try {
                Event event = Event.GSON.fromJson(log.getRawPayload(), Event.class);
                webhookController.handleEvent(event);
                auditLogService.markProcessed(log.getId());
            } catch (Exception ex) {
                auditLogService.markFailed(log.getId(), ex.getMessage(), true);
            }
        }
    }
}
