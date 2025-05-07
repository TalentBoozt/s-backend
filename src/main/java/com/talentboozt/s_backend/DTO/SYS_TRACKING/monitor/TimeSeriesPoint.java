package com.talentboozt.s_backend.DTO.SYS_TRACKING.monitor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeSeriesPoint {
    private Instant timestamp;
    private long count;
}
