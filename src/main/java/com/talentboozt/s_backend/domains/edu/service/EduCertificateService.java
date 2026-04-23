package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.exception.EduBadRequestException;
import com.talentboozt.s_backend.domains.edu.exception.EduResourceNotFoundException;
import com.talentboozt.s_backend.domains.edu.model.ECertificates;
import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.model.EEnrollments;
import com.talentboozt.s_backend.domains.edu.model.EUser;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ECertificatesRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ECoursesRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EEnrollmentsRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EUserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class EduCertificateService {

        private final ECertificatesRepository certificatesRepository;
        private final EEnrollmentsRepository enrollmentsRepository;
        private final ECoursesRepository coursesRepository;
        private final EUserRepository userRepository;
        private final R2StorageService storageService;

        public EduCertificateService(ECertificatesRepository certificatesRepository,
                        EEnrollmentsRepository enrollmentsRepository,
                        ECoursesRepository coursesRepository,
                        EUserRepository userRepository,
                        R2StorageService storageService) {
                this.certificatesRepository = certificatesRepository;
                this.enrollmentsRepository = enrollmentsRepository;
                this.coursesRepository = coursesRepository;
                this.userRepository = userRepository;
                this.storageService = storageService;
        }

        public ECertificates generateCertificate(String enrollmentId) {
                EEnrollments enrollment = enrollmentsRepository.findById(enrollmentId)
                                .orElseThrow(() -> new EduResourceNotFoundException("Enrollment not found with id: " + enrollmentId));

                if (!Boolean.TRUE.equals(enrollment.getCompleted())) {
                        throw new EduBadRequestException("Cannot issue certificate: Course not completed");
                }

                ECourses course = coursesRepository.findById(enrollment.getCourseId())
                                .orElseThrow(() -> new EduResourceNotFoundException("Course not found with id: " + enrollment.getCourseId()));

                EUser user = userRepository.findById(enrollment.getUserId())
                                .orElseThrow(() -> new EduResourceNotFoundException("User not found with id: " + enrollment.getUserId()));

                // Check if already generated
                boolean alreadyExists = certificatesRepository.findByUserId(user.getId()).stream()
                                .anyMatch(c -> course.getId().equals(c.getCourseId()));

                if (alreadyExists) {
                        return certificatesRepository.findByUserId(user.getId()).stream()
                                        .filter(c -> course.getId().equals(c.getCourseId())).findFirst().get();
                }

                String certificateId = "CERT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                
                // Real-world: Generate PDF here. Mock: Upload a small verification file
                String mockContent = "Certificate ID: " + certificateId + "\nRecipient: " + user.getDisplayName() + "\nCourse: " + course.getTitle();
                String certificateUrl;
                try {
                        certificateUrl = storageService.uploadFile(mockContent.getBytes(), certificateId + ".txt", "text/plain");
                } catch (java.io.IOException e) {
                        throw new RuntimeException("Cloud Storage Error: Failed to upload certificate", e);
                }

                ECertificates certificate = ECertificates.builder()
                                .courseId(course.getId())
                                .userId(user.getId())
                                .creatorId(course.getCreatorId())
                                .courseName(course.getTitle())
                                .recipientName(user.getDisplayName() != null ? user.getDisplayName() : "Learner")
                                .certificateId(certificateId)
                                .url(certificateUrl)
                                .templateId("DEFAULT_TEMPLATE")
                                .isVerified(true)
                                .shareableLink("https://edu.talnova.com/verify/" + certificateId)
                                .issuedAt(Instant.now())
                                .build();

                return certificatesRepository.save(certificate);
        }

        public ECertificates verifyCertificate(String certificateId) {
                return certificatesRepository.findByCertificateId(certificateId)
                                .orElseThrow(() -> new EduResourceNotFoundException("Invalid Certificate ID: " + certificateId));
        }

        public List<ECertificates> getUserCertificates(String userId) {
                return certificatesRepository.findByUserId(userId);
        }
}
