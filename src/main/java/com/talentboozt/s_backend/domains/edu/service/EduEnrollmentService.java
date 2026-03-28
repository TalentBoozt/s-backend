package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.dto.enrollment.EnrollmentRequest;
import com.talentboozt.s_backend.domains.edu.enums.EPaymentStatus;
import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.model.EEnrollments;
import com.talentboozt.s_backend.domains.edu.model.ETransactions;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ECoursesRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EEnrollmentsRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ETransactionsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EduEnrollmentService {

    private final EEnrollmentsRepository enrollmentsRepository;
    private final ETransactionsRepository transactionsRepository;
    private final ECoursesRepository coursesRepository;

    public EduEnrollmentService(EEnrollmentsRepository enrollmentsRepository,
            ETransactionsRepository transactionsRepository,
            ECoursesRepository coursesRepository) {
        this.enrollmentsRepository = enrollmentsRepository;
        this.transactionsRepository = transactionsRepository;
        this.coursesRepository = coursesRepository;
    }

    @Transactional
    public EEnrollments enrollInCourse(String userId, EnrollmentRequest request) {
        // Prevent duplicate enrollments
        boolean alreadyEnrolled = enrollmentsRepository.findAll().stream()
                .anyMatch(e -> userId.equals(e.getUserId()) && request.getCourseId().equals(e.getCourseId()));

        if (alreadyEnrolled) {
            throw new RuntimeException("User is already enrolled in this course");
        }

        ECourses course = coursesRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!Boolean.TRUE.equals(course.getPublished())) {
            throw new RuntimeException("Course is not available for enrollment");
        }

        double price = course.getPrice() != null ? course.getPrice() : 0.0;

        // Handle Transaction if Paid Course
        if (price > 0 && request.getPaymentMethod() != null) {
            ETransactions transaction = ETransactions.builder()
                    .buyerId(userId)
                    .sellerId(course.getCreatorId())
                    .courseId(course.getId())
                    .amount(price)
                    .currency(course.getCurrency())
                    .platformFee(price * 0.1) // 10% platform fee simulation
                    .creatorEarning(price * 0.9)
                    .paymentMethod(request.getPaymentMethod())
                    .paymentStatus(EPaymentStatus.SUCCESS) // Simulate success for now
                    .paymentGateway(request.getPaymentGatewayId())
                    .transactionId(UUID.randomUUID().toString())
                    .createdAt(Instant.now())
                    .build();
            transactionsRepository.save(transaction);
        }

        // Create Enrollment
        EEnrollments enrollment = EEnrollments.builder()
                .userId(userId)
                .courseId(course.getId())
                .workspaceId(course.getWorkspaceId())
                .source(request.getSource() != null ? request.getSource() : "MARKETPLACE")
                .progress(0)
                .completedLessons(0)
                .totalLessons(course.getTotalLessons() != null ? course.getTotalLessons() : 0)
                .completed(false)
                .enrolledAt(Instant.now())
                .createdAt(Instant.now())
                .build();

        EEnrollments saved = enrollmentsRepository.save(enrollment);

        // Update Course Total Enrollments
        course.setTotalEnrollments((course.getTotalEnrollments() != null ? course.getTotalEnrollments() : 0) + 1);
        coursesRepository.save(course);

        return saved;
    }

    public List<EEnrollments> getUserEnrollments(String userId) {
        return enrollmentsRepository.findAll().stream()
                .filter(e -> userId.equals(e.getUserId()))
                .collect(Collectors.toList());
    }

    public EEnrollments getEnrollmentDetails(String enrollmentId) {
        return enrollmentsRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
    }

    public EEnrollments getEnrollmentByCourseAndUser(String courseId, String userId) {
        return enrollmentsRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new RuntimeException("Enrollment for course/user not found"));
    }

    public EEnrollments recordProgress(String enrollmentId, String lessonId, Long watchTime) {
        EEnrollments enrollment = getEnrollmentDetails(enrollmentId);

        // Update watch time if provided
        if (watchTime != null) {
            long current = enrollment.getTotalWatchTime() != null ? enrollment.getTotalWatchTime() : 0L;
            enrollment.setTotalWatchTime(current + watchTime);
        }

        // Simplified progress incrementing
        enrollment.setCompletedLessons(enrollment.getCompletedLessons() + 1);
        enrollment.setLastAccessedLessonId(lessonId);
        enrollment.setLastAccessedAt(Instant.now());

        if (enrollment.getTotalLessons() > 0) {
            int progress = (int) Math.round((enrollment.getCompletedLessons() * 100.0) / enrollment.getTotalLessons());
            enrollment.setProgress(Math.min(progress, 100));
            if (enrollment.getProgress() >= 100) {
                enrollment.setCompleted(true);
                enrollment.setCompletedAt(Instant.now());
            }
        }

        return enrollmentsRepository.save(enrollment);
    }
}
