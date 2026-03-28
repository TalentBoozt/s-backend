package com.talentboozt.s_backend.domains.edu.dto.engagement;

import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Data
public class ReviewRequest {
    @Min(1)
    @Max(5)
    private Double rating;
    @NotBlank
    private String content;
}
