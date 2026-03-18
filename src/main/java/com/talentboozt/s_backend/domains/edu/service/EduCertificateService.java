package com.talentboozt.s_backend.domains.edu.service;

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

        public EduCertificateService(ECertificatesRepository certificatesRepository,
                        EEnrollmentsRepository enrollmentsRepository,
                        ECoursesRepository coursesRepository,
                        EUserRepository userRepository) {
                this.certificatesRepository = certificatesRepository;
                this.enrollmentsRepository = enrollmentsRepository;
                this.coursesRepository = coursesRepository;
                this.userRepository = userRepository;
        }

        public ECertificates generateCertificate(String enrollmentId) {
                EEnrollments enrollment = enrollmentsRepository.findById(enrollmentId)
                                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

                if (!Boolean.TRUE.equals(enrollment.getCompleted())) {
                        throw new RuntimeException("Cannot issue certificate: Course not completed");
                }

                ECourses course = coursesRepository.findById(enrollment.getCourseId())
                                .orElseThrow(() -> new RuntimeException("Course not found"));

                EUser user = userRepository.findById(enrollment.getUserId())
                                .orElseThrow(() -> new RuntimeException("User not found"));

                // Check if already generated
                boolean alreadyExists = certificatesRepository.findByUserId(user.getId()).stream()
                                .anyMatch(c -> course.getId().equals(c.getCourseId()));

                if (alreadyExists) {
                        return certificatesRepository.findByUserId(user.getId()).stream()
                                        .filter(c -> course.getId().equals(c.getCourseId())).findFirst().get();
                }

                String certificateId = "CERT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

                ECertificates certificate = ECertificates.builder()
                                .courseId(course.getId())
                                .userId(user.getId())
                                .creatorId(course.getCreatorId())
                                .courseName(course.getTitle())
                                .recipientName(user.getDisplayName())
                                .certificateId(certificateId)
                                // In a real app we'd map this to a PDF generator S3 Bucket URI link
                                .url("https://talnova-certificates.s3.amazonaws.com/" + certificateId + ".pdf")
                                .templateId("DEFAULT_TEMPLATE")
                                .isVerified(true)
                                .shareableLink("https://edu.talnova.com/verify/" + certificateId)
                                .issuedAt(Instant.now())
                                .build();

                return certificatesRepository.save(certificate);
        }

        public ECertificates verifyCertificate(String certificateId) {
                return certificatesRepository.findByCertificateId(certificateId)
                                .orElseThrow(() -> new RuntimeException("Invalid Certificate ID"));
        }

        public List<ECertificates> getUserCertificates(String userId) {
                return certificatesRepository.findByUserId(userId);
        }
}
