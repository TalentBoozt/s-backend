package com.talentboozt.s_backend.domains.user.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter

@Document(collection = "trainer_profiles")
public class TrainerProfile {
    @Id
    private String id;
    @Indexed(unique = true)
    private String employeeId;
    private String headline;
    private String bio;
    private List<String> specialties;
    private List<String> languages;
    private String hourlyRate;
    private String availability;
    private List<String> certifications;
    private Double rating;
    private Integer totalReviews;
    private String trainerVideoIntro;
    private String website;
    private String linkedIn;
    private String youtube;
    private boolean publicProfile = true;
}

