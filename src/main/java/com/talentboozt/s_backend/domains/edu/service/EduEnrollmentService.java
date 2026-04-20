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

    public EduEnrollmentService(EEnrollmentsRepository enrollmentsRepository,
            ECoursesRepository coursesRepository) {
        this.enrollmentsRepository = enrollmentsRepository;
        this.coursesRepository = coursesRepository;
    }

    /**
     * Free courses only. Paid courses must use Stripe checkout + {@link #ensureEnrollmentAfterSuccessfulPurchase}.
     */
    @Transactional
    public EEnrollments enrollInCourse(String userId, EnrollmentRequest request) {
        ECourses course = coursesRepository.findById(request.getCourseId())
                .orElseThrow(() -> new EduResourceNotFoundException("Course not found with id: " + request.getCourseId()));

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
     * Called after Stripe reports payment succeeded (webhook or confirm endpoint). Idempotent.
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
     * Gift / redemption path: enroll without payment when the course is publicly available.
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

                    course.setTotalEnrollments((course.getTotalEnrollments() != null ? course.getTotalEnrollments() : 0) + 1);
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
                .orElseThrow(() -> new EduResourceNotFoundException("Enrollment for course " + courseId + " and user " + userId + " not found"));
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
        java.util.Set<String> completedSet = enrollment.getCompletedLessonIds() != null ? 
            new java.util.HashSet<>(java.util.Arrays.asList(enrollment.getCompletedLessonIds())) : 
            new java.util.HashSet<>();

        if (lessonId != null && !completedSet.contains(lessonId)) {
            completedSet.add(lessonId);
            enrollment.setCompletedLessonIds(completedSet.toArray(new String[0]));
            int count = completedSet.size();
            enrollment.setCompletedLessons(count);

            if (enrollment.getTotalLessons() > 0) {
                int progress = (int) Math.round((count * 100.0) / enrollment.getTotalLessons());
                enrollment.setProgress(Math.min(progress, 100));
                if (enrollment.getProgress() >= 100 && !Boolean.TRUE.equals(enrollment.getCompleted())) {
                    enrollment.setCompleted(true);
                    enrollment.setCompletedAt(Instant.now());
                }
            }
        }

        enrollment.setLastAccessedLessonId(lessonId);
        enrollment.setLastAccessedAt(Instant.now());

        return enrollmentsRepository.save(enrollment);
    }
}
