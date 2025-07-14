package com.talentboozt.s_backend.shared.mail.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailJob {
    private final String to;
    private final String subject;
    private final String htmlBody;
    private int retryCount = 0;

    public EmailJob(String to, String subject, String htmlBody) {
        this.to = to;
        this.subject = subject;
        this.htmlBody = htmlBody;
    }
}
