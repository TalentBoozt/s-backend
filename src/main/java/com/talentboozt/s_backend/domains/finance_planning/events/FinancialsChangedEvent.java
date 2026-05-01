package com.talentboozt.s_backend.domains.finance_planning.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class FinancialsChangedEvent extends ApplicationEvent {
    private final String organizationId;
    private final String projectId;
    private final String scenarioId;
    private final String userId;
    private final java.util.List<String> changedFields;

    public FinancialsChangedEvent(Object source, String organizationId, String projectId, String scenarioId, String userId, java.util.List<String> changedFields) {
        super(source);
        this.organizationId = organizationId;
        this.projectId = projectId;
        this.scenarioId = scenarioId;
        this.userId = userId;
        this.changedFields = changedFields;
    }
}
