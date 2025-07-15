package com.talentboozt.s_backend.domains.plat_courses.service;

import com.talentboozt.s_backend.domains.plat_courses.dto.CertificateDTO;
import com.talentboozt.s_backend.domains.plat_courses.dto.CourseEnrollment;
import com.talentboozt.s_backend.domains.plat_courses.model.CourseCertificateModel;
import com.talentboozt.s_backend.domains.plat_courses.model.EmpCoursesModel;
import com.talentboozt.s_backend.domains.plat_courses.repository.CourseCertificateRepository;
import com.talentboozt.s_backend.domains.plat_courses.repository.EmpCoursesRepository;
import com.talentboozt.s_backend.domains.user.dto.EmpCertificatesDTO;
import com.talentboozt.s_backend.domains.user.model.EmpCertificatesModel;
import com.talentboozt.s_backend.domains.user.service.EmpCertificatesService;
import com.talentboozt.s_backend.shared.mail.service.EmailService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Year;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

@Service
public class CertificateProcessorService {

    private final EmpCertificatesService empCertificatesService;
    private final CourseCertificateRepository courseCertificateRepository;
    private final EmailService emailService;
    private final EmpCoursesRepository empCoursesRepository;

    public CertificateProcessorService(
            EmpCertificatesService empCertificatesService,
            CourseCertificateRepository courseCertificateRepository,
            EmailService emailService,
            EmpCoursesRepository empCoursesRepository
    ) {
        this.empCertificatesService = empCertificatesService;
        this.courseCertificateRepository = courseCertificateRepository;
        this.emailService = emailService;
        this.empCoursesRepository = empCoursesRepository;
    }

    public void processToIssueCertificate(CourseEnrollment course, CertificateDTO certDTO, String employeeId, String courseId) {
        // 1. Add to emp_certificates
        EmpCertificatesModel empCertificatesModel = empCertificatesService.getByEmployeeId(employeeId);
        if (empCertificatesModel == null) {
            empCertificatesModel = new EmpCertificatesModel();
            empCertificatesModel.setEmployeeId(employeeId);
            empCertificatesModel.setCertificates(new ArrayList<>());
        }

        EmpCertificatesDTO cert = new EmpCertificatesDTO();
        cert.setId(certDTO.getCertificateId());
        cert.setName(course.getCourseName());
        cert.setOrganization(certDTO.getIssuedBy());
        cert.setDate(certDTO.getIssuedDate());
        cert.setCertificateId(certDTO.getCertificateId());
        cert.setCertificateUrl(certDTO.getUrl());

        empCertificatesModel.getCertificates().add(cert);
        empCertificatesService.addEmpCertificates(empCertificatesModel);

        // 2. Add to course_certificates
        CourseCertificateModel courseCert = new CourseCertificateModel();
        courseCert.setEmployeeId(employeeId);
        courseCert.setCourseId(courseId);
        courseCert.setCertificateId(certDTO.getCertificateId());
        courseCert.setType(certDTO.getType());
        courseCert.setUrl(certDTO.getUrl());
        courseCert.setIssuedBy(certDTO.getIssuedBy());
        courseCert.setIssuedDate(certDTO.getIssuedDate());
        courseCert.setDelivered(certDTO.isDelivered());
        courseCert.setFileName(certDTO.getFileName());
        courseCert.setDescription(certDTO.getDescription());

        courseCertificateRepository.save(courseCert);

        // 3. Send email
        empCoursesRepository.findByEmployeeId(employeeId).ifPresent(empCourses -> {
            String to = empCourses.getEmail();
            String subject = "🎉 Course Completed: " + course.getCourseName();
            Map<String, String> variables = Map.of(
                    "courseName", course.getCourseName(),
                    "courseId", courseId,
                    "year", String.valueOf(Year.now().getValue())
            );
            try {
                emailService.sendCourseCompletionEmail(to, subject, variables);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void proceedToUpdateSystemCert(CertificateDTO certificateDTO, String employeeId, String courseId) {
        empCertificatesService.updateCertificate(employeeId, certificateDTO);
        updateCertificateInCourse(employeeId, courseId, certificateDTO);
    }

    private void updateCertificateInCourse(String employeeId, String courseId, CertificateDTO certificateDTO) {
        Optional<EmpCoursesModel> optional = empCoursesRepository.findByEmployeeId(employeeId);
        if (optional.isEmpty()) return;

        EmpCoursesModel coursesModel = optional.get();

        for (CourseEnrollment course : coursesModel.getCourses()) {
            if (course.getCourseId().equals(courseId)) {
                for (CertificateDTO cert : course.getCertificates()) {
                    if (cert.getCertificateId().equals(certificateDTO.getCertificateId())) {
                        cert.setUrl(certificateDTO.getUrl());
                        cert.setDelivered(true);
                        cert.setFileName(certificateDTO.getFileName());
                        break;
                    }
                }
                break;
            }
        }
        empCoursesRepository.save(coursesModel);
    }
}
