package com.talentboozt.s_backend.shared.audit.tracking.dto.monitor;

import lombok.Data;

@Data
public class EventTypeCount {
    private String eventType;
    private Long count;
}