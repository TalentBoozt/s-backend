package com.talentboozt.s_backend.domains.activity.application.event;

import com.talentboozt.s_backend.domains.activity.service.JPActivityService;
import com.talentboozt.s_backend.domains.pipeline.domain.event.CandidateStageChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CandidateStageChangedActivityListener {

    private final JPActivityService activityService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    @Order(2)
    public void onCandidateStageChanged(CandidateStageChangedEvent event) {
        activityService.logActivity(
                event.recruiterId(),
                null,
                "STAGE_CHANGED",
                "Candidate Stage Updated",
                "Candidate moved to " + event.toStage(),
                "PIPELINE",
                Map.of("applicationId", event.applicationId()));

        System.out.println("Candidate " + event.applicationId() + " moved to " + event.toStage());
    }
}
