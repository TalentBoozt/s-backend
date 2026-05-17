package com.talentboozt.s_backend.shared.realtime.adapter;

import com.talentboozt.s_backend.domains.pipeline.domain.event.CandidateStageChangedEvent;
import com.talentboozt.s_backend.shared.realtime.service.RealtimeBroadcaster;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Broadcasts pipeline stage changes to the org WebSocket topic (same payload shape as before).
 */
@Component
@RequiredArgsConstructor
public class CandidateStageChangedRealtimeListener {

    private final RealtimeBroadcaster realtimeBroadcaster;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    @Order(1)
    public void onCandidateStageChanged(CandidateStageChangedEvent event) {
        realtimeBroadcaster.broadcastToOrganization(
                event.recruiterId(),
                "pipeline",
                event);
    }
}
