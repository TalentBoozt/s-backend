package com.talentboozt.s_backend.domains.plat_courses.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter

@Document(collection = "course_certificates")
public class CourseCertificateModel {
    @Id
    private String id;
    private String employeeId;
    private String courseId;
    private String certificateId;
    private String type;         // system / trainer
    private String url;
    private String issuedBy;
    private String issuedDate;
    private boolean delivered;
    private String fileName;
    private String description;
}
