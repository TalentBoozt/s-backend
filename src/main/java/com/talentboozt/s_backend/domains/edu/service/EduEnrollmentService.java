package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.dto.enrollment.EnrollmentRequest;
import com.talentboozt.s_backend.domains.edu.enums.ECourseStatus;
import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.model.EEnrollments;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ECoursesRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EEnrollmentsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class EduEnrollmentService {

    private final EEnrollmentsRepository enrollmentsRepository;
    private final ECoursesRepository coursesRepository;

    public EduEnrollmentService(EEnrollmentsRepository enrollmentsRepository,
            ECoursesRepository coursesRepository) {
        this.enrollmentsRepository = enrollmentsRepository;
        this.coursesRepository = coursesRepository;
    }

    /**
     * Free courses only. Paid courses must use Stripe checkout +
     * {@link #ensureEnrollmentAfterSuccessfulPurchase}.
     */
    @Transactional
    public EEnrollments enrollInCourse(String userId, EnrollmentRequest request) {
        ECourses course = coursesRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!Boolean.TRUE.equals(course.getPublished())) {
            throw new RuntimeException("Course is not available for enrollment");
        }
        if (!isApprovedForLearners(course)) {
            throw new RuntimeException("Course is not available for enrollment");
        }

        double price = course.getPrice() != null ? course.getPrice() : 0.0;
        if (price > 0) {
            throw new RuntimeException(
                    "This course requires payment. Complete checkout with Stripe — direct enrollment is not allowed for paid courses.");
        }

        return createEnrollmentIfAbsent(userId, course,
                request.getSource() != null ? request.getSource() : "MARKETPLACE");
    }

    /**
     * Called after Stripe reports payment succeeded (webhook or confirm endpoint).
     * Idempotent.
     */
    @Transactional
    public EEnrollments ensureEnrollmentAfterSuccessfulPurchase(String userId, String courseId) {
        ECourses course = coursesRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        if (!Boolean.TRUE.equals(course.getPublished()) || !isApprovedForLearners(course)) {
            throw new RuntimeException("Course is not available for enrollment");
        }
        return createEnrollmentIfAbsent(userId, course, "MARKETPLACE");
    }

    private static boolean isApprovedForLearners(ECourses course) {
        if (course.getStatus() == ECourseStatus.PUBLISHED) {
            return true;
        }
        return course.getStatus() == null && Boolean.TRUE.equals(course.getPublished());
    }

    /**
     * Gift / redemption path: enroll without payment when the course is publicly
     * available.
     */
    @Transactional
    public EEnrollments enrollFromGift(String userId, String courseId) {
        ECourses course = coursesRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        if (!Boolean.TRUE.equals(course.getPublished()) || !isApprovedForLearners(course)) {
            throw new RuntimeException("Course is not available for enrollment");
        }
        return createEnrollmentIfAbsent(userId, course, "GIFT");
    }

    private EEnrollments createEnrollmentIfAbsent(String userId, ECourses course, String source) {
        return enrollmentsRepository.findByUserIdAndCourseId(userId, course.getId())
                .orElseGet(() -> {
                    EEnrollments enrollment = EEnrollments.builder()
                            .userId(userId)
                            .courseId(course.getId())
                            .workspaceId(course.getWorkspaceId())
                            .source(source)
                            .progress(0)
                            .completedLessons(0)
                            .totalLessons(course.getTotalLessons() != null ? course.getTotalLessons() : 0)
                            .completed(false)
                            .enrolledAt(Instant.now())
                            .createdAt(Instant.now())
                            .build();
                    EEnrollments saved = enrollmentsRepository.save(enrollment);

                    course.setTotalEnrollments(
                            (course.getTotalEnrollments() != null ? course.getTotalEnrollments() : 0) + 1);
                    coursesRepository.save(course);
                    return saved;
                });
    }

    public List<EEnrollments> getUserEnrollments(String userId) {
        return enrollmentsRepository.findByUserId(userId);
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

        if (watchTime != null) {
            long current = enrollment.getTotalWatchTime() != null ? enrollment.getTotalWatchTime() : 0L;
            enrollment.setTotalWatchTime(current + watchTime);
        }

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
