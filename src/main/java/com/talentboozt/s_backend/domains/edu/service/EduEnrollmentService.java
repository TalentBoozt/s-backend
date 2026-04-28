package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.dto.enrollment.EnrollmentRequest;
import com.talentboozt.s_backend.domains.edu.enums.ECourseStatus;
import com.talentboozt.s_backend.domains.edu.exception.EduBadRequestException;
import com.talentboozt.s_backend.domains.edu.exception.EduResourceNotFoundException;
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
    private final EduCertificateService certificateService;
    private final com.talentboozt.s_backend.domains.edu.repository.mongodb.ELessonsRepository lessonsRepository;
    private final EduAnalyticsEventService analyticsEventService;
    private final EduTrustScoreService trustScoreService;

    public EduEnrollmentService(EEnrollmentsRepository enrollmentsRepository,
            ECoursesRepository coursesRepository,
            EduCertificateService certificateService,
            com.talentboozt.s_backend.domains.edu.repository.mongodb.ELessonsRepository lessonsRepository,
            EduAnalyticsEventService analyticsEventService,
            EduTrustScoreService trustScoreService) {
        this.enrollmentsRepository = enrollmentsRepository;
        this.coursesRepository = coursesRepository;
        this.certificateService = certificateService;
        this.lessonsRepository = lessonsRepository;
        this.analyticsEventService = analyticsEventService;
        this.trustScoreService = trustScoreService;
    }

    /**
     * Free courses only. Paid courses must use Stripe checkout +
     * {@link #ensureEnrollmentAfterSuccessfulPurchase}.
     */
    @Transactional
    public EEnrollments enrollInCourse(String userId, EnrollmentRequest request) {
        ECourses course = coursesRepository.findById(request.getCourseId())
                .orElseThrow(
                        () -> new EduResourceNotFoundException("Course not found with id: " + request.getCourseId()));

        if (!Boolean.TRUE.equals(course.getPublished())) {
            throw new EduBadRequestException("Course is not available for enrollment");
        }
        if (!isApprovedForLearners(course)) {
            throw new EduBadRequestException("Course is not available for enrollment");
        }

        double price = course.getPrice() != null ? course.getPrice() : 0.0;
        if (price > 0) {
            throw new EduBadRequestException(
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
                .orElseThrow(() -> new EduResourceNotFoundException("Course not found with id: " + courseId));
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
                .orElseThrow(() -> new EduResourceNotFoundException("Course not found with id: " + courseId));
        if (!Boolean.TRUE.equals(course.getPublished()) || !isApprovedForLearners(course)) {
            throw new EduBadRequestException("Course is not available for enrollment");
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
        return enrollmentsRepository.findByUserId(userId).stream()
                .map(this::populateCourse)
                .collect(java.util.stream.Collectors.toList());
    }

    public EEnrollments getEnrollmentDetails(String enrollmentId) {
        EEnrollments enrollment = enrollmentsRepository.findById(enrollmentId)
                .orElseThrow(() -> new EduResourceNotFoundException("Enrollment not found with id: " + enrollmentId));
        return populateCourse(enrollment);
    }

    public EEnrollments getEnrollmentByCourseAndUser(String courseId, String userId) {
        EEnrollments enrollment = enrollmentsRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new EduResourceNotFoundException(
                        "Enrollment for course " + courseId + " and user " + userId + " not found"));
        return populateCourse(enrollment);
    }

    private EEnrollments populateCourse(EEnrollments enrollment) {
        if (enrollment != null && enrollment.getCourseId() != null) {
            coursesRepository.findById(enrollment.getCourseId()).ifPresent(enrollment::setCourse);
        }
        return enrollment;
    }

    public EEnrollments recordProgress(String enrollmentId, String lessonId, Long watchTime) {
        EEnrollments enrollment = getEnrollmentDetails(enrollmentId);

        if (watchTime != null && watchTime > 0) {
            long current = enrollment.getTotalWatchTime() != null ? enrollment.getTotalWatchTime() : 0L;
            enrollment.setTotalWatchTime(current + watchTime);
        }

        // Handle unique lesson completion
        java.util.Set<String> completedSet = enrollment.getCompletedLessonIds() != null
                ? new java.util.HashSet<>(java.util.Arrays.asList(enrollment.getCompletedLessonIds()))
                : new java.util.HashSet<>();

        if (lessonId != null && !completedSet.contains(lessonId)) {
            completedSet.add(lessonId);
            enrollment.setCompletedLessonIds(completedSet.toArray(new String[0]));
            int count = completedSet.size();
            enrollment.setCompletedLessons(count);

            int total = 0;
            if (enrollment.getCourse() != null && enrollment.getCourse().getTotalLessons() != null
                    && enrollment.getCourse().getTotalLessons() > 0) {
                total = enrollment.getCourse().getTotalLessons();
            } else if (enrollment.getTotalLessons() != null && enrollment.getTotalLessons() > 0) {
                total = enrollment.getTotalLessons();
            } else {
                // Fallback: count lessons from DB
                total = (int) lessonsRepository.countByCourseId(enrollment.getCourseId());
            }

            if (total > 0) {
                int progress = (int) Math.round((count * 100.0) / total);
                enrollment.setProgress(Math.min(progress, 100));
                if (enrollment.getProgress() >= 100 && !Boolean.TRUE.equals(enrollment.getCompleted())) {
                    enrollment.setCompleted(true);
                    enrollment.setCompletedAt(Instant.now());

                    // Tracking: Course Completion
                    analyticsEventService.recordEvent(
                            com.talentboozt.s_backend.domains.edu.enums.EAnalyticsEvent.COMPLETE,
                            enrollment.getUserId(), enrollment.getCourseId(),
                            java.util.Map.of("enrollmentId", enrollmentId));

                    // Auto-trigger certificate generation
                    try {
                        certificateService.generateCertificate(enrollmentId);
                    } catch (Exception e) {
                        // Log error but don't fail the progress recording
                        System.err.println("Failed to auto-generate certificate for enrollment " + enrollmentId + ": "
                                + e.getMessage());
                    }
                }
            }
            // Update enrollment's totalLessons too just to keep it somewhat in sync
            if (total > 0)
                enrollment.setTotalLessons(total);
        }

        enrollment.setLastAccessedLessonId(lessonId);
        enrollment.setLastAccessedAt(Instant.now());

        EEnrollments saved = enrollmentsRepository.save(enrollment);

        // Update Trust Score if completed
        if (Boolean.TRUE.equals(saved.getCompleted()) && saved.getCourse() != null) {
            trustScoreService.updateScore(saved.getCourse().getCreatorId());
        }

        return saved;
    }

    @Transactional
    public EEnrollments completeEnrollment(String enrollmentId) {
        EEnrollments enrollment = getEnrollmentDetails(enrollmentId);

        // Recalculate total lessons just in case
        int total = 0;
        if (enrollment.getCourse() != null && enrollment.getCourse().getTotalLessons() != null
                && enrollment.getCourse().getTotalLessons() > 0) {
            total = enrollment.getCourse().getTotalLessons();
        } else if (enrollment.getTotalLessons() != null && enrollment.getTotalLessons() > 0) {
            total = enrollment.getTotalLessons();
        } else {
            total = (int) lessonsRepository.countByCourseId(enrollment.getCourseId());
        }

        if (total > 0)
            enrollment.setTotalLessons(total);

        enrollment.setCompletedLessons(total);
        enrollment.setProgress(100);
        enrollment.setCompleted(true);
        enrollment.setCompletedAt(Instant.now());
        enrollment.setLastAccessedAt(Instant.now());

        EEnrollments saved = enrollmentsRepository.save(enrollment);

        // Update Trust Score
        if (saved.getCourse() != null) {
            trustScoreService.updateScore(saved.getCourse().getCreatorId());
        }

        // Tracking: Course Completion (Manual)
        analyticsEventService.recordEvent(com.talentboozt.s_backend.domains.edu.enums.EAnalyticsEvent.COMPLETE,
                enrollment.getUserId(), enrollment.getCourseId(),
                java.util.Map.of("enrollmentId", enrollmentId, "status", "MANUAL"));

        // Ensure certificate is generated
        try {
            certificateService.generateCertificate(enrollmentId);
        } catch (Exception e) {
            System.err.println("Manual completion: Failed to auto-generate certificate for enrollment " + enrollmentId
                    + ": " + e.getMessage());
        }

        return saved;
    }
}
