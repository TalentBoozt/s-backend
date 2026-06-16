package com.talentboozt.s_backend.domains.portal.courses.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {
    private int rating;        // 1–5 stars
    private String comment;    // optional user comment
    private String reviewedAt; // ISO timestamp
}
