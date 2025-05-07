package com.talentboozt.s_backend.DTO.SYS_TRACKING.monitor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionViewDTO {
    private String sessionId;
    private String userId;
    private List<String> urls;
    private long duration;
    private long eventCount;
}
