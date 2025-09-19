package com.talentboozt.s_backend.domains.com_courses.model;

import com.talentboozt.s_backend.domains.com_courses.dto.RecModuleDTO;
import com.talentboozt.s_backend.domains.com_courses.dto.RecordedCourseReviewDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@Setter
@Document(collection = "recorded_courses")
public class RecordedCourseModel {

    @Id
    private String id;

    private String title;
    private String subtitle;
    private String description;

    private String courseType = "recorded"; // constant/fixed

    private String price;  // can be string or BigDecimal
    private boolean published;
    private boolean approved;

    private String createdAt;
    private String updatedAt;

    @Field("modules")
    private List<RecModuleDTO> modules;

    private String image;

    private List<String> skills;
    private List<String> requirements;

    private String lecturer;  // instructor/trainer name
    private String email;
    private String language = "English"; // Optional, default to "English"
    private String category;  // e.g., "Development", "Design"

    @Field("reviews")
    private List<RecordedCourseReviewDTO> reviews;

    private double rating = 0.0; // average rating (computed)
    private int reviewCount = 0; // number of reviews

    private boolean certificate; // true if certificate available after completion

    private String currency = "USD"; // pricing currency
}
