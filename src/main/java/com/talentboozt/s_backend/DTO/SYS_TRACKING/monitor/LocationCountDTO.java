package com.talentboozt.s_backend.DTO.SYS_TRACKING.monitor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationCountDTO {
    private String country;
    private String city;
    private long count;
}
