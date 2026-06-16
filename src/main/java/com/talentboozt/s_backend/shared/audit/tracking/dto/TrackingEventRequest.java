package com.talentboozt.s_backend.shared.audit.tracking.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class TrackingEventRequest {
    private List<Map<String, Object>> events;
}