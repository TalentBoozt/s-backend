package com.talentboozt.s_backend.domains.plat_courses.service;

import com.talentboozt.s_backend.domains.com_courses.dto.InstallmentDTO;
import com.talentboozt.s_backend.domains.com_courses.dto.ModuleDTO;
import com.talentboozt.s_backend.domains.plat_courses.dto.*;
import com.talentboozt.s_backend.domains.com_courses.model.CourseModel;
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

    public EmpCoursesModel addEmpCourses(EmpCoursesModel empCourses, String type) {
        List<EmpCoursesModel> empCoursesList = getEmpCoursesByEmployeeId(empCourses.getEmployeeId());
        EmpCoursesModel empCoursesModel;
        if (!empCoursesList.isEmpty()) {
            empCoursesModel = empCoursesList.get(0);
            List<CourseEnrollment> courses = empCoursesModel.getCourses();
            List<RecordedCourseEnrollment> recordedCourses = empCoursesModel.getRecordedCourses();
            if (courses == null) {
                courses = new ArrayList<>(); // Initialize the courses list if it's null
            }
            if (recordedCourses == null) {
                recordedCourses = new ArrayList<>(); // Initialize the recordedCourses list if it's null
            }
            if (type.equals("recorded")) {
                recordedCourses.addAll(empCourses.getRecordedCourses());
                empCoursesModel.setRecordedCourses(recordedCourses);
            } else {
                courses.addAll(empCourses.getCourses());
                empCoursesModel.setCourses(courses);
            }
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
            empCoursesModel.setRecordedCourses(empCourses.getRecordedCourses());
            return empCoursesRepository.save(empCoursesModel);
        }
        return null;
    }

    public void deleteEmpCourses(String id) { empCoursesRepository.deleteById(id); }

    public EmpCoursesModel deleteEmpCourse(String employeeId, String courseId, String type) {
        List<EmpCoursesModel> empCoursesList = getEmpCoursesByEmployeeId(employeeId);
        if (!empCoursesList.isEmpty()) {
            EmpCoursesModel empCoursesModel = empCoursesList.get(0);
            List<CourseEnrollment> courses = empCoursesModel.getCourses();
            List<RecordedCourseEnrollment> recordedCourses = empCoursesModel.getRecordedCourses();
            if (type.equals("recorded")) {
                if (recordedCourses != null) {
                    recordedCourses.removeIf(course -> course.getCourseId().equals(courseId));
                    empCoursesModel.setRecordedCourses(recordedCourses);
                    return empCoursesRepository.save(empCoursesModel);
                }
            } else {
                if (courses != null) {
                    courses.removeIf(course -> course.getCourseId().equals(courseId));
                    empCoursesModel.setCourses(courses);
                    return empCoursesRepository.save(empCoursesModel);
                }
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

    public EmpCoursesModel updateFullCoursePayment(String userId, String courseId, String installmentId, String paid) {
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
                        return empCoursesRepository.save(empCoursesModel);
                    }
                }
            }
        }
        throw new RuntimeException("Employee not found for id: " + userId);
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
                            CertificateDTO certDTO = buildSystemCertificate(course.getCourseName());
                            if (course.getCertificates() == null) {
                                course.setCertificates(new ArrayList<>());
                            }
                            boolean exists = course.getCertificates().stream()
                                    .anyMatch(c -> c.getCertificateId().equals(certDTO.getCertificateId()));
                            if (!exists) {
                                course.getCertificates().add(certDTO);
                            }

                            certificateProcessorService.processToIssueCertificate(course.getCourseName(), certDTO, employeeId, courseId);
                        }

                        return empCoursesRepository.save(empCoursesModel);
                    }
                }
            }
        }
        throw new RuntimeException("Employee not found for id: " + employeeId);
    }

    private CertificateDTO buildSystemCertificate(String courseName) {
        CertificateDTO certificate = new CertificateDTO();
        certificate.setCertificateId(UUID.randomUUID().toString());
        certificate.setType("system");
        certificate.setUrl(""); // To be set from frontend
        certificate.setIssuedBy("Talentboozt");
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        certificate.setIssuedDate(OffsetDateTime.now(ZoneOffset.UTC).format(formatter));
        certificate.setDelivered(false);
        certificate.setFileName(""); // To be set from frontend
        certificate.setDescription("System-generated certificate for " + courseName);
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

    public RecordedCourseEnrollment getRecordedCourseByEmployeeIdAndCourseId(String employeeId, String courseId) {
        List<EmpCoursesModel> empCoursesList = getEmpCoursesByEmployeeId(employeeId);
        if (!empCoursesList.isEmpty()) {
            List<RecordedCourseEnrollment> recordedCourses = empCoursesList.get(0).getRecordedCourses();
            if (recordedCourses != null) {
                for (RecordedCourseEnrollment course : recordedCourses) {
                    if (course.getCourseId().equals(courseId)) {
                        return course;
                    }
                }
            }
        }
        throw new RuntimeException("Recorded course not found for employeeId: " + employeeId);
    }

    public EmpCoursesModel updateRecordedCourseProgress(String employeeId, String courseId, CourseUpdateDTO courseUpdate) {
        List<EmpCoursesModel> empCoursesList = getEmpCoursesByEmployeeId(employeeId);
        if (!empCoursesList.isEmpty()) {
            EmpCoursesModel model = empCoursesList.get(0);
            List<RecordedCourseEnrollment> recordedCourses = model.getRecordedCourses();
            if (recordedCourses != null) {
                for (RecordedCourseEnrollment course : recordedCourses) {
                    if (course.getCourseId().equals(courseId)) {
                        course.setCourseProgress(courseUpdate.getCourseProgress());
                        course.setModuleProgress(courseUpdate.getModuleProgress());
                        return empCoursesRepository.save(model);
                    }
                }
            }
        }
        throw new RuntimeException("Recorded course not found for progress update.");
    }

    public EmpCoursesModel updateRecordedCourseReview(String employeeId, String courseId, ReviewDTO review) {
        List<EmpCoursesModel> empCoursesList = getEmpCoursesByEmployeeId(employeeId);
        if (!empCoursesList.isEmpty()) {
            EmpCoursesModel model = empCoursesList.get(0);
            List<RecordedCourseEnrollment> recordedCourses = model.getRecordedCourses();
            if (recordedCourses != null) {
                for (RecordedCourseEnrollment course : recordedCourses) {
                    if (course.getCourseId().equals(courseId)) {
                        course.setReview(review);
                        return empCoursesRepository.save(model);
                    }
                }
            }
        }
        throw new RuntimeException("Recorded course not found for review update.");
    }

    public EmpCoursesModel completeRecordedCourse(String employeeId, String courseId) {
        List<EmpCoursesModel> empCoursesList = getEmpCoursesByEmployeeId(employeeId);
        if (!empCoursesList.isEmpty()) {
            EmpCoursesModel model = empCoursesList.get(0);
            List<RecordedCourseEnrollment> recordedCourses = model.getRecordedCourses();
            if (recordedCourses != null) {
                for (RecordedCourseEnrollment course : recordedCourses) {
                    if (course.getCourseId().equals(courseId)) {
                        course.setStatus("completed");
                        CertificateDTO certDTO = buildSystemCertificate(course.getCourseName());
                        if (course.getCertificates() == null) {
                            course.setCertificates(new ArrayList<>());
                        }
                        boolean exists = course.getCertificates().stream()
                                .anyMatch(c -> c.getCertificateId().equals(certDTO.getCertificateId()));
                        if (!exists) {
                            course.getCertificates().add(certDTO);
                        }
                        certificateProcessorService.processToIssueCertificate(course.getCourseName(), certDTO, employeeId, courseId);
                        return empCoursesRepository.save(model);
                    }
                }
            }
        }
        throw new RuntimeException("Recorded course not found to mark as completed.");
    }

    public EmpCoursesModel updateRecordedCoursePayment(String employeeId, String courseId, String status) {
        List<EmpCoursesModel> empCoursesList = getEmpCoursesByEmployeeId(employeeId);
        if (!empCoursesList.isEmpty()) {
            EmpCoursesModel model = empCoursesList.get(0);
            List<RecordedCourseEnrollment> recordedCourses = model.getRecordedCourses();
            if (recordedCourses != null) {
                for (RecordedCourseEnrollment course : recordedCourses) {
                    if (course.getCourseId().equals(courseId)) {
                        course.setStatus(status);
                        return empCoursesRepository.save(model);
                    }
                }
            }
        }
        throw new RuntimeException("Recorded course not found for payment update.");
    }

    @Async
    public CompletableFuture<List<EmpCoursesModel>> getEmpCoursesByEmployeeIdAsync(String employeeId) {
        List<EmpCoursesModel> empCourses = getEmpCoursesByEmployeeId(employeeId);
        return CompletableFuture.completedFuture(empCourses);
    }

    public EmpCoursesModel getEmpCourseByEmployeeIdAndCourseId(String employeeId, String courseId) {
        List<EmpCoursesModel> empCourses = getEmpCoursesByEmployeeId(employeeId);
        if (!empCourses.isEmpty()) {
            EmpCoursesModel empCoursesModel = empCourses.get(0);
            List<CourseEnrollment> courses = empCoursesModel.getCourses();
            if (courses != null) {
                for (CourseEnrollment course : courses) {
                    if (course.getCourseId().equals(courseId)) {
                        return empCoursesModel;
                    }
                }
            }
        }
        throw new RuntimeException("Employee not found for id: " + employeeId);
    }
}
