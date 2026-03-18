package com.talentboozt.s_backend.domains.edu.dto.thirdparty;

import lombok.Data;

@Data
public class GiftRequest {
    private String senderId;
    private String recipientEmail;
    private String courseId;
    private String personalMessage;
}
