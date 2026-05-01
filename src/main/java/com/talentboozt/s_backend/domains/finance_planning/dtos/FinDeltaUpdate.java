package com.talentboozt.s_backend.domains.finance_planning.dtos;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class FinDeltaUpdate {
    private String type;
    private String path;
    private Object value;
    private Integer version;
    private Instant timestamp;
    private String userId;
}
