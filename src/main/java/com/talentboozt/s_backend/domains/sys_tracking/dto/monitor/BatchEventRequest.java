package com.talentboozt.s_backend.domains.sys_tracking.dto.monitor;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class BatchEventRequest {
    private List<Map<String, Object>> events;
}
