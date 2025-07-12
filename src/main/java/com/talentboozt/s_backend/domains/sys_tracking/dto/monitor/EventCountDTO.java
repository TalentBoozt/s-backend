package com.talentboozt.s_backend.domains.sys_tracking.dto.monitor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventCountDTO {
    private String eventType;
    private long count;
}
