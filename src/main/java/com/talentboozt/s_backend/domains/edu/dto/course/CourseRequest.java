package com.talentboozt.s_backend.domains.edu.dto.course;

import com.talentboozt.s_backend.domains.edu.enums.ECourseLevel;
import com.talentboozt.s_backend.domains.edu.enums.ECourseType;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class CourseRequest {
    @NotBlank
    private String title;
    @Size(max = 5000)
    private String description;
    private String shortDescription;
    private ECourseType type;
    private String language;
    private ECourseLevel level;
    private String[] categories;
    private String[] subCategories;
    private Double price;
    private String currency;
    private Boolean isPrivate;
}
