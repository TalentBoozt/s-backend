package com.talentboozt.s_backend.domains.com_courses.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CourseResponseDTO {
    private String id;
    private String companyId;
    private String name;
    private String description;
    private String overview;
    private String category;
    private String organizer;
    private String level;
    private String currency;
    private String price;
    private boolean onetimePayment;
    private List<InstallmentDTO> installment;
    private String duration;
    private List<ModuleDTO> modules;
    private String rating;
    private String language;
    private String lecturer;
    private String image;
    private List<String> skills;
    private List<String> requirements;
    private boolean certificate;
    private String platform;
    private String location;
    private String startDate;
    private String fromTime;
    private String toTime;
    private String utcStart;
    private String utcEnd;
    private String trainerTimezone;
    private String courseStatus;
    private String paymentMethod;
    private boolean publicity;
    private List<MaterialsDTO> materials;
    private List<QuizDTO> quizzes;
    private String batchId;
}
