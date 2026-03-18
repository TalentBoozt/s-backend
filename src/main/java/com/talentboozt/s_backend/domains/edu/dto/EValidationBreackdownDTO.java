package com.talentboozt.s_backend.domains.edu.dto;

import lombok.Data;

@Data
public class EValidationBreackdownDTO {
    private Integer structure;
    private Integer clarity;
    private Integer engagement;
    private Integer accuracy;
    private Integer completeness;
    private Integer relevance;
    private Integer originality;
    private Integer language;
    private Integer formatting;
    private Integer references;
    private Integer totalScore;
    private String status;
    private String comments;
}
