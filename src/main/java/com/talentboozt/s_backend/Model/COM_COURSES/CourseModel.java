package com.talentboozt.s_backend.Model.COM_COURSES;

import com.talentboozt.s_backend.DTO.COM_COURSES.InstallmentDTO;
import com.talentboozt.s_backend.DTO.COM_COURSES.ModuleDTO;
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
    private String courseStatus;
    private String paymentMethod;
}
