package com.talentboozt.s_backend.domains.edu.dto.monetization;

import lombok.Data;
import java.util.List;

@Data
public class MultiCourseCheckoutRequest {
    private String userId;
    private List<String> courseIds;
}
