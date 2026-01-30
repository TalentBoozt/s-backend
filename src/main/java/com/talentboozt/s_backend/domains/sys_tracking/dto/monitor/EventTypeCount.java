package com.talentboozt.s_backend.domains.sys_tracking.dto.monitor;

import lombok.Data;

@Data
public class EventTypeCount {
    private String eventType;
    private Long count;
}