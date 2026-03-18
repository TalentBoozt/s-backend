package com.talentboozt.s_backend.domains.edu.dto.evaluation;

import lombok.Data;

@Data
public class SubmissionRequest {
    private String content;     
    private String[] attachmentUrls;
}
