package com.talentboozt.s_backend.domains.edu.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioReviewDTO {
    private String id;
    private String reviewerName;
    private String content;
    private Integer rating;
    private String courseName;
}
