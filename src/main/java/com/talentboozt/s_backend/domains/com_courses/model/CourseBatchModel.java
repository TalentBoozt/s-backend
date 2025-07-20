package com.talentboozt.s_backend.domains.com_courses.model;

import com.talentboozt.s_backend.domains.com_courses.dto.InstallmentDTO;
import com.talentboozt.s_backend.domains.com_courses.dto.MaterialsDTO;
import com.talentboozt.s_backend.domains.com_courses.dto.ModuleDTO;
import com.talentboozt.s_backend.domains.com_courses.dto.QuizDTO;
import com.talentboozt.s_backend.domains.plat_courses.dto.CertificateDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter

@Document(collection = "course_batches")
public class CourseBatchModel {
    @Id
    private String id;
    private String courseId;  // reference to CourseModel
    private String batchName; // e.g. "July 2025 Batch"
    private String startDate;
    private String fromTime;
    private String toTime;
    private String utcStart;
    private String utcEnd;
    private String trainerTimezone;
    private String courseStatus;
    private boolean publicity;

    private String currency;
    private String price;
    private boolean onetimePayment;
    private String paymentMethod; // e.g. "Credit Card", "PayPal"

    private String duration; // e.g. "3 months", "6 weeks"
    private String language; // e.g. "English", "Spanish"
    private String platform; // e.g. "Online", "In-person"
    private String location; // e.g. "New York", "Online"
    private String image; // URL or path to the course image
    private String lecturer; // e.g. "John Doe"

    private List<InstallmentDTO> installment;
    private List<ModuleDTO> modules;
    private List<MaterialsDTO> materials;
    private List<QuizDTO> quizzes;

    private List<String> enrolledUserIds;
}
