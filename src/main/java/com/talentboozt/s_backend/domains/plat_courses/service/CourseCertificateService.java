package com.talentboozt.s_backend.domains.plat_courses.service;

import com.talentboozt.s_backend.domains.plat_courses.dto.CertificateDTO;
import com.talentboozt.s_backend.domains.plat_courses.dto.CourseEnrollment;
import com.talentboozt.s_backend.domains.plat_courses.model.CourseCertificateModel;
import com.talentboozt.s_backend.domains.plat_courses.model.EmpCoursesModel;
import com.talentboozt.s_backend.domains.plat_courses.repository.CourseCertificateRepository;
import com.talentboozt.s_backend.domains.plat_courses.repository.EmpCoursesRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class CourseCertificateService {

    private final CourseCertificateRepository courseCertificateRepository;
    private final EmpCoursesRepository empCoursesRepository;
    private final EmpCoursesService empCoursesService;
    private final CertificateProcessorService certificateProcessorService;

    public CourseCertificateService(EmpCoursesService empCoursesService, CourseCertificateRepository courseCertificateRepository,
                                    EmpCoursesRepository empCoursesRepository, CertificateProcessorService certificateProcessorService) {
        this.courseCertificateRepository = courseCertificateRepository;
        this.empCoursesRepository = empCoursesRepository;
        this.empCoursesService = empCoursesService;
        this.certificateProcessorService = certificateProcessorService;
    }

    public CourseCertificateModel addCertificate(CourseCertificateModel certificate) {
        if (certificate == null || certificate.getCourseId() == null || certificate.getEmployeeId().isEmpty()) {
            return null;
        }
        List<EmpCoursesModel> empCoursesList = empCoursesService.getEmpCoursesByEmployeeId(certificate.getEmployeeId());
        if (!empCoursesList.isEmpty()) {
            EmpCoursesModel empCoursesModel = empCoursesList.get(0);
            List<CourseEnrollment> courses = empCoursesModel.getCourses();
            if (courses != null) {
                for (CourseEnrollment course : courses) {
                    if (course.getCourseId().equals(certificate.getCourseId())) {
                        course.setStatus("completed");

                        CertificateDTO certDTO = buildTrainerCertificate(course);
                        if (course.getCertificates() == null) {
                            course.setCertificates(new ArrayList<>());
                        }
                        boolean exists = course.getCertificates().stream()
                                .anyMatch(c -> c.getCertificateId().equals(certDTO.getCertificateId()));
                        if (!exists) {
                            course.getCertificates().add(certDTO);
                        }

                        certificateProcessorService.processToIssueCertificate(course, certDTO, certificate.getEmployeeId(), certificate.getCourseId());

                        empCoursesRepository.save(empCoursesModel);
                    }
                }
            }
        }
        return courseCertificateRepository.save(certificate);
    }

    private CertificateDTO buildTrainerCertificate(CourseEnrollment course) {
        CertificateDTO certificate = new CertificateDTO();
        certificate.setCertificateId(UUID.randomUUID().toString());
        certificate.setType(certificate.getType());
        certificate.setUrl(certificate.getUrl());
        certificate.setIssuedBy(course.getOrganizer());
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        certificate.setIssuedDate(OffsetDateTime.now(ZoneOffset.UTC).format(formatter));
        certificate.setDelivered(false);
        certificate.setFileName(certificate.getFileName());
        certificate.setDescription(certificate.getDescription());
        return certificate;
    }

    public Iterable<CourseCertificateModel> getAllCertificates() {
        return courseCertificateRepository.findAll();
    }

    public CourseCertificateModel getCertificate(String id) {
        return courseCertificateRepository.findById(id).orElse(null);
    }

    public Iterable<CourseCertificateModel> getCertificatesByCourseId(String courseId) {
        Optional<List<CourseCertificateModel>> certificates = courseCertificateRepository.findAllByCourseId(courseId);
        return certificates.orElse(null);
    }

    public Iterable<CourseCertificateModel> getCertificatesByEmployeeId(String employeeId) {
        Optional<List<CourseCertificateModel>> certificates = courseCertificateRepository.findAllByEmployeeId(employeeId);
        return certificates.orElse(null);
    }

    public CourseCertificateModel getCertificatesByCertificateId(String certificateId) {
        Optional<CourseCertificateModel> certificate = courseCertificateRepository.findByCertificateId(certificateId);
        return certificate.orElse(null);
    }

    public Iterable<CourseCertificateModel> getCertificatesByType(String type) {
        Optional<List<CourseCertificateModel>> certificates = courseCertificateRepository.findAllByType(type);
        return certificates.orElse(null);
    }

    public Iterable<CourseCertificateModel> getCertificatesByDelivered(boolean delivered) {
        Optional<List<CourseCertificateModel>> certificates = courseCertificateRepository.findAllByDelivered(delivered);
        return certificates.orElse(null);
    }

    public CourseCertificateModel updateSystemCertificate(String id, CourseCertificateModel certificate) { // after user generates the certificate from frontend
        if (certificate == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Certificate not found");
        }
        CourseCertificateModel existingCertificate = courseCertificateRepository.findById(id).orElse(null);
        return updateSysCert(existingCertificate, certificate);
    }

    public CourseCertificateModel updateSystemCertificateByCertificateId(String certificateId, CourseCertificateModel certificate) {
        if (certificate == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Certificate not found");
        }
        CourseCertificateModel existingCertificate = courseCertificateRepository.findByCertificateId(certificateId).orElse(null);
        return updateSysCert(existingCertificate, certificate);
    }

    private CourseCertificateModel updateSysCert(CourseCertificateModel existingCertificate, CourseCertificateModel certificate) {
        if (existingCertificate == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Certificate not found");
        }
        if (!Objects.equals(existingCertificate.getEmployeeId(), certificate.getEmployeeId()) ||
                !Objects.equals(existingCertificate.getCourseId(), certificate.getCourseId()) ||
                !Objects.equals(existingCertificate.getCertificateId(), certificate.getCertificateId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Required fields do not match");
        }
        existingCertificate.setUrl(certificate.getUrl());
        existingCertificate.setDelivered(true);
        existingCertificate.setFileName(certificate.getFileName());

        CertificateDTO certDTO = new CertificateDTO();
        certDTO.setCertificateId(certificate.getCertificateId());
        certDTO.setType(certificate.getType());
        certDTO.setUrl(certificate.getUrl());
        certDTO.setIssuedBy(certificate.getIssuedBy());
        certDTO.setIssuedDate(certificate.getIssuedDate());
        certDTO.setDescription(certificate.getDescription());
        certDTO.setDelivered(true);
        certDTO.setFileName(certificate.getFileName());
        certDTO.setLinkedinShared(false);
        certificateProcessorService.proceedToUpdateSystemCert(certDTO, certificate.getEmployeeId(), certificate.getCourseId());
        return courseCertificateRepository.save(existingCertificate);
    }
}
