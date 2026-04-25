package com.talentboozt.s_backend.domains.edu.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioCourseDTO {
    private String id;
    private String title;
    private String thumbnail;
    private String category;
    private Double rating;
    private Integer totalStudents;
    private Double price;
}
