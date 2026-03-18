package com.talentboozt.s_backend.domains.edu.dto.engagement;

import lombok.Data;

@Data
public class ReviewRequest {
    private Double rating;
    private String content;
}
