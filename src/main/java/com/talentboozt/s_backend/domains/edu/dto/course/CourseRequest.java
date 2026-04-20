package com.talentboozt.s_backend.domains.edu.dto.course;

import com.talentboozt.s_backend.domains.edu.enums.ECourseContentType;
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
    private String thumbnail;
    private String previewVideoUrl;
    private ECourseType type;
    private ECourseContentType contentType;
    private String language;
    private ECourseLevel level;
    private String[] tags;
    private String[] categories;
    private String[] subCategories;
    private String[] keywords;
    private String[] skills;
    private String[] prerequisites;
    private String[] outcomes;
    private Double price;
    private Double compareAtPrice;
    private String currency;
    private Boolean isPrivate;
    private Boolean isFeatured;
    private Boolean isTrending;
    private Integer searchRank;
}
