package com.talentboozt.s_backend.domains.pipeline.application;

import com.talentboozt.s_backend.domains.pipeline.domain.event.CandidateStageChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * Publishes pipeline lifecycle events for other bounded contexts to consume.
 */
@Service
@RequiredArgsConstructor
public class PipelineEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publishCandidateStageChanged(
            String applicationId,
            String fromStage,
            String toStage,
            String recruiterId) {
        eventPublisher.publishEvent(new CandidateStageChangedEvent(applicationId, fromStage, toStage, recruiterId));
    }
}
