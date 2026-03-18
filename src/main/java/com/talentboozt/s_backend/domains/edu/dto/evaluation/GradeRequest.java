package com.talentboozt.s_backend.domains.edu.dto.evaluation;

import com.talentboozt.s_backend.domains.edu.enums.EGradingStatus;
import lombok.Data;

@Data
public class GradeRequest {
    private Double score;
    private EGradingStatus status;
    private String feedback;
}
