package com.talentboozt.s_backend.domains.pipeline.domain.event;

/**
 * Fired when a candidate's pipeline stage changes. Payload is identifiers and stage labels only.
 */
public record CandidateStageChangedEvent(
        String applicationId,
        String fromStage,
        String toStage,
        String recruiterId) {
}
