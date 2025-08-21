package com.talentboozt.s_backend.domains.plat_courses.service;

import com.talentboozt.s_backend.domains.com_courses.dto.InstallmentDTO;
import com.talentboozt.s_backend.domains.com_courses.dto.ModuleDTO;
import com.talentboozt.s_backend.domains.plat_courses.dto.CertificateDTO;
import com.talentboozt.s_backend.domains.plat_courses.dto.CertificateDetailsDTO;
import com.talentboozt.s_backend.domains.plat_courses.dto.CourseEnrollment;
import com.talentboozt.s_backend.domains.com_courses.model.CourseModel;
import com.talentboozt.s_backend.domains.plat_courses.dto.PaymentDetailsDTO;
import com.talentboozt.s_backend.domains.plat_courses.model.EmpCoursesModel;
import com.talentboozt.s_backend.domains.plat_courses.repository.EmpCoursesRepository;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class EmpCoursesService {

    private final EmpCoursesRepository empCoursesRepository;
    private final CertificateProcessorService certificateProcessorService;

    public List<EmpCoursesModel> getAllEmpCourses() {
        return empCoursesRepository.findAll();
    }

    public EmpCoursesService(EmpCoursesRepository empCoursesRepository, CertificateProcessorService certificateProcessorService) {
        this.empCoursesRepository = empCoursesRepository;
        this.certificateProcessorService = certificateProcessorService;
    }

    public List<EmpCoursesModel> getEmpCoursesByEmployeeId(@NonNull String employeeId) {
        return empCoursesRepository.findAllByEmployeeId(employeeId);
    }

    public EmpCoursesModel addEmpCourses(EmpCoursesModel empCourses) {
        List<EmpCoursesModel> empCoursesList = getEmpCoursesByEmployeeId(empCourses.getEmployeeId());
        EmpCoursesModel empCoursesModel;
        if (!empCoursesList.isEmpty()) {
            empCoursesModel = empCoursesList.get(0);
            List<CourseEnrollment> courses = empCoursesModel.getCourses();
            if (courses == null) {
                courses = new ArrayList<>(); // Initialize the courses list if it's null
            }
            courses.addAll(empCourses.getCourses());
            empCoursesModel.setCourses(courses);
        } else {
            empCoursesModel = empCoursesRepository.save(empCourses);
        }

        empCoursesRepository.save(empCoursesModel);

        return empCoursesModel;
    }

    public EmpCoursesModel updateEmpCourses(String id, EmpCoursesModel empCourses) {
        EmpCoursesModel empCoursesModel = empCoursesRepository.findById(id).orElse(null);
        if (empCoursesModel != null) {
            empCoursesModel.setEmployeeId(empCourses.getEmployeeId());
            empCoursesModel.setCourses(empCourses.getCourses());
            return empCoursesRepository.save(empCoursesModel);
        }
        return null;
    }

    public void deleteEmpCourses(String id) { empCoursesRepository.deleteById(id); }

    public EmpCoursesModel deleteEmpCourse(String employeeId, String courseId) {
        List<EmpCoursesModel> empCoursesList = getEmpCoursesByEmployeeId(employeeId);
        if (!empCoursesList.isEmpty()) {
            EmpCoursesModel empCoursesModel = empCoursesList.get(0);
            List<CourseEnrollment> courses = empCoursesModel.getCourses();
            if (courses != null) {
                courses.removeIf(course -> course.getCourseId().equals(courseId));
                empCoursesModel.setCourses(courses);
                return empCoursesRepository.save(empCoursesModel);
            }
            return empCoursesModel;
        }
        throw new RuntimeException("Employee not found for id: " + employeeId);
    }

    public EmpCoursesModel editEmpCourse(String employeeId, CourseModel course) {
        List<EmpCoursesModel> empCoursesList = getEmpCoursesByEmployeeId(employeeId);
        if (!empCoursesList.isEmpty()) {
            EmpCoursesModel empCoursesModel = empCoursesList.get(0);
            List<CourseEnrollment> courses = empCoursesModel.getCourses();
            if (courses != null) {
                for (CourseEnrollment c : courses) {
                    if (c.getCourseId().equals(course.getId())) {
                        c.setCourseName(course.getName());
                        c.setOverview(course.getOverview());
                        c.setOrganizer(course.getOrganizer());
                        c.setModules(course.getModules());
                        c.setInstallment(course.getInstallment());
                        c.setImage(course.getImage());
                        c.setEnrollmentDate(LocalDateTime.now().toString());
                        c.setStatus("Enrolled");
                        break;
                    }
                }
            }
            return empCoursesRepository.save(empCoursesModel);
        }
        throw new RuntimeException("Employee not found for id: " + employeeId);
    }

    public EmpCoursesModel updateModulePayment(String employeeId, String courseId, String moduleId, String status) {
        List<EmpCoursesModel> empCoursesList = getEmpCoursesByEmployeeId(employeeId);
        if (!empCoursesList.isEmpty()) {
            EmpCoursesModel empCoursesModel = empCoursesList.get(0);
            List<CourseEnrollment> courses = empCoursesModel.getCourses();
            if (courses != null) {
                for (CourseEnrollment c : courses) {
                    if (c.getCourseId().equals(courseId)) {
                        for (ModuleDTO m : c.getModules()) {
                            if (m.getId().equals(moduleId)) {
                                m.setPaid(status);
                                return empCoursesRepository.save(empCoursesModel);
                            }
                        }
                    }
                }
            }
        }
        throw new RuntimeException("Employee not found for id: " + employeeId);
    }

    public EmpCoursesModel updateInstallmentPayment(String employeeId, String courseId, String installmentId, String status){
        List<EmpCoursesModel> empCoursesList = getEmpCoursesByEmployeeId(employeeId);
        if (!empCoursesList.isEmpty()) {
            EmpCoursesModel empCoursesModel = empCoursesList.get(0);
            List<CourseEnrollment> courses = empCoursesModel.getCourses();
            if (courses != null) {
                for (CourseEnrollment c : courses) {
                    if (c.getCourseId().equals(courseId)) {
                        for (InstallmentDTO i : c.getInstallment()) {
                            if (i.getId().equals(installmentId)) {
                                i.setPaid(status);
                                if (status.equals("pending")) i.setRequestDate(LocalDateTime.now().toString());
                                if (status.equals("paid") || status.equals("cancelled")) i.setPaymentDate(LocalDateTime.now().toString());
                            }
                        }
                        for (ModuleDTO m : c.getModules()) {
                            if (m.getInstallmentId().equals(installmentId)) {
                                m.setPaid("true");
                            }
                        }
                        return empCoursesRepository.save(empCoursesModel);
                    }
                }
            }
        }
        throw new RuntimeException("Employee not found for id: " + employeeId);
    }

    public void updateFullCoursePayment(String userId, String courseId, String installmentId, String paid) {
        List<EmpCoursesModel> empCoursesList = getEmpCoursesByEmployeeId(userId);
        if (!empCoursesList.isEmpty()) {
            EmpCoursesModel empCoursesModel = empCoursesList.get(0);
            List<CourseEnrollment> courses = empCoursesModel.getCourses();
            if (courses != null) {
                for (CourseEnrollment c : courses) {
                    if (c.getCourseId().equals(courseId)) {
                        for (InstallmentDTO i : c.getInstallment()) {
                            if (i.getId().equals(installmentId)) {
                                i.setPaid(paid);
                                if (paid.equals("pending")) i.setRequestDate(LocalDateTime.now().toString());
                                if (paid.equals("paid") || paid.equals("cancelled")) i.setPaymentDate(LocalDateTime.now().toString());
                            }
                        }
                        for (ModuleDTO m : c.getModules()) {
                            m.setPaid("true");
                        }
                        empCoursesRepository.save(empCoursesModel);
                    }
                }
            }
        }
    }

    public EmpCoursesModel updateEnrollmentStatus(String employeeId, String courseId, String status) {
        List<EmpCoursesModel> empCoursesList = getEmpCoursesByEmployeeId(employeeId);
        if (!empCoursesList.isEmpty()) {
            EmpCoursesModel empCoursesModel = empCoursesList.get(0);
            List<CourseEnrollment> courses = empCoursesModel.getCourses();
            if (courses != null) {
                for (CourseEnrollment course : courses) {
                    if (course.getCourseId().equals(courseId)) {
                        course.setStatus(status);

                        if ("completed".equalsIgnoreCase(status)) {
                            CertificateDTO certDTO = buildSystemCertificate(course);
                            if (course.getCertificates() == null) {
                                course.setCertificates(new ArrayList<>());
                            }
                            boolean exists = course.getCertificates().stream()
                                    .anyMatch(c -> c.getCertificateId().equals(certDTO.getCertificateId()));
                            if (!exists) {
                                course.getCertificates().add(certDTO);
                            }

                            certificateProcessorService.processToIssueCertificate(course, certDTO, employeeId, courseId);
                        }

                        return empCoursesRepository.save(empCoursesModel);
                    }
                }
            }
        }
        throw new RuntimeException("Employee not found for id: " + employeeId);
    }

    private CertificateDTO buildSystemCertificate(CourseEnrollment course) {
        CertificateDTO certificate = new CertificateDTO();
        certificate.setCertificateId(UUID.randomUUID().toString());
        certificate.setType("system");
        certificate.setUrl(""); // To be set from frontend
        certificate.setIssuedBy("Talentboozt");
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        certificate.setIssuedDate(OffsetDateTime.now(ZoneOffset.UTC).format(formatter));
        certificate.setDelivered(false);
        certificate.setFileName(""); // To be set from frontend
        certificate.setDescription("System-generated certificate for " + course.getCourseName());
        return certificate;
    }

    public List<PaymentDetailsDTO> getAllPaymentDetails() {
        List<PaymentDetailsDTO> payments = new ArrayList<>();

        for (EmpCoursesModel user : empCoursesRepository.findAll()) {
            if (user.getCourses() == null) continue;

            for (CourseEnrollment course : user.getCourses()) {
                List<InstallmentDTO> installments = course.getInstallment();

                if (installments == null || installments.isEmpty()) {
                    // Free course or no installment defined
                    payments.add(new PaymentDetailsDTO(
                            user.getId(),
                            user.getEmployeeName(),
                            user.getEmail(),
                            course.getCourseId(),
                            course.getCourseName(),
                            null,
                            "FREE Course",
                            "0",
                            "USD",
                            "paid",
                            "N/A",
                            course.getEnrollmentDate()
                    ));
                } else {
                    for (InstallmentDTO inst : installments) {
                        payments.add(new PaymentDetailsDTO(
                                user.getId(),
                                user.getEmployeeName(),
                                user.getEmail(),
                                course.getCourseId(),
                                course.getCourseName(),
                                inst.getId(),
                                inst.getName(),
                                inst.getPrice(),
                                inst.getCurrency(),
                                inst.getPaid(),
                                inst.getPaymentMethod(),
                                inst.getPaymentDate()
                        ));
                    }
                }
            }
        }
        return payments;
    }

    public List<CertificateDetailsDTO> getAllCertificateDetails() {
        List<CertificateDetailsDTO> result = new ArrayList<>();

        for (EmpCoursesModel user : empCoursesRepository.findAll()) {
            if (user.getCourses() == null) continue;

            for (CourseEnrollment course : user.getCourses()) {
                List<CertificateDTO> certificates = course.getCertificates();

                if (certificates == null || certificates.isEmpty()) continue;

                for (CertificateDTO cert : certificates) {
                    result.add(new CertificateDetailsDTO(
                            user.getId(),
                            user.getEmployeeName(),
                            user.getEmail(),
                            course.getCourseId(),
                            course.getCourseName(),
                            cert.getCertificateId(),
                            cert.getFileName(),
                            cert.getType(),
                            cert.getUrl(),
                            cert.getIssuedBy(),
                            cert.getIssuedDate(),
                            cert.isDelivered(),
                            cert.isLinkedinShared()
                    ));
                }
            }
        }
        return result;
    }

    @Async
    public CompletableFuture<List<EmpCoursesModel>> getEmpCoursesByEmployeeIdAsync(String employeeId) {
        List<EmpCoursesModel> empCourses = getEmpCoursesByEmployeeId(employeeId);
        return CompletableFuture.completedFuture(empCourses);
    }
}
