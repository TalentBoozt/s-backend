package com.talentboozt.s_backend.domains.com_courses.model;

import com.talentboozt.s_backend.domains.com_courses.dto.InstallmentDTO;
import com.talentboozt.s_backend.domains.com_courses.dto.MaterialsDTO;
import com.talentboozt.s_backend.domains.com_courses.dto.ModuleDTO;
import com.talentboozt.s_backend.domains.com_courses.dto.QuizDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@Setter

@Document(collection = "job_hunter_courses")
public class CourseModel {
    @Id
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
    @Field("installment")
    private List<InstallmentDTO> installment;
    private String duration;
    @Field("modules")
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
    private String utcStart;  // UTC ISO string
    private String utcEnd;
    private String trainerTimezone;
    private String courseStatus;
    private String paymentMethod;
    private boolean publicity;
    private List<MaterialsDTO> materials;
    @Field("quizzes")
    private List<QuizDTO> quizzes;
}
