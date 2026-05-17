package com.talentboozt.s_backend.domains.notifications.application.event;

import com.talentboozt.s_backend.domains.pipeline.domain.event.CandidateStageChangedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Consumes hiring pipeline stage changes. No notification is sent today (legacy HiringEventListener
 * injected NotificationService but never called it); this listener preserves that behavior.
 */
@Component
public class CandidateStageChangedNotificationListener {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    @Order(3)
    public void onCandidateStageChanged(CandidateStageChangedEvent event) {
        // Intentionally empty: stage-change notifications not implemented in previous flow.
    }
}
