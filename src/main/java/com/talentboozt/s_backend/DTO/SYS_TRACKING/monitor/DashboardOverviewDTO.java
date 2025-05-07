package com.talentboozt.s_backend.DTO.SYS_TRACKING.monitor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardOverviewDTO {
    private long activeUsers;
    private long dailyLogins;
    private long sessionCount;
    private long errorCount;
}
