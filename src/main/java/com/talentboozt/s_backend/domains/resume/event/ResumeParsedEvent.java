package com.talentboozt.s_backend.domains.resume.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ResumeParsedEvent extends ApplicationEvent {
    private final String resumeId;
    private final String employeeId;

    public ResumeParsedEvent(Object source, String resumeId, String employeeId) {
        super(source);
        this.resumeId = resumeId;
        this.employeeId = employeeId;
    }
}
