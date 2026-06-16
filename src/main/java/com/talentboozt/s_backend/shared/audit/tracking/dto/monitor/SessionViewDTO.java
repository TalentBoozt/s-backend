package com.talentboozt.s_backend.shared.audit.tracking.dto.monitor;

import lombok.Data;
import java.util.List;

@Data
public class SessionViewDTO {
    private String sessionId;
    private String userId;
    private List<String> urls;
    private Long duration;
    private Integer eventCount;
}