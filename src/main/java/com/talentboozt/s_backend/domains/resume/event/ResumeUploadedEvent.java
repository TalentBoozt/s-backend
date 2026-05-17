package com.talentboozt.s_backend.domains.resume.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ResumeUploadedEvent extends ApplicationEvent {
    private final String resumeId;
    private final String employeeId;
    private final String fileUrl;

    public ResumeUploadedEvent(Object source, String resumeId, String employeeId, String fileUrl) {
        super(source);
        this.resumeId = resumeId;
        this.employeeId = employeeId;
        this.fileUrl = fileUrl;
    }
}
