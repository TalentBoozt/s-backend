package com.talentboozt.s_backend.domains.sys_tracking.dto.monitor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginLocationAggregateDTO {
    private double latitude;
    private double longitude;
    private long value;
}
