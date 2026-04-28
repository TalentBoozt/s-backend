package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.enums.ECourseStatus;
import com.talentboozt.s_backend.domains.edu.exception.EduBadRequestException;
import com.talentboozt.s_backend.domains.edu.exception.EduResourceNotFoundException;
import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.model.EduCourseReviewLog;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ECoursesRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EduCourseReviewLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class EduCourseLifecycleService {

    private final ECoursesRepository courseRepository;
    private final EduCourseReviewLogRepository reviewLogRepository;
    private final EduAccessGuardService accessGuard;

    @Transactional
    public ECourses submitCourse(String creatorId, String courseId) {
        accessGuard.enforceCourseOwnership(creatorId, courseId);
        ECourses course = getCourseById(courseId);

        if (course.getStatus() != ECourseStatus.DRAFT && course.getStatus() != ECourseStatus.REJECTED) {
            throw new EduBadRequestException("Only courses in DRAFT or REJECTED status can be submitted.");
        }

        course.setStatus(ECourseStatus.SUBMITTED);
        course.setUpdatedAt(Instant.now());
        log.info("Course {} submitted for review by creator {}", courseId, creatorId);
        return courseRepository.save(course);
    }

    @Transactional
    public ECourses startReview(String reviewerId, String courseId) {
        // Role check should be handled at controller/security level
        ECourses course = getCourseById(courseId);

        if (course.getStatus() != ECourseStatus.SUBMITTED) {
            throw new EduBadRequestException("Course must be in SUBMITTED status to start review.");
        }

        course.setStatus(ECourseStatus.UNDER_REVIEW);
        course.setUpdatedAt(Instant.now());
        
        logReview(courseId, reviewerId, "STARTED_REVIEW", null);
        log.info("Reviewer {} started review for course {}", reviewerId, courseId);
        return courseRepository.save(course);
    }

    @Transactional
    public ECourses approveCourse(String reviewerId, String courseId) {
        ECourses course = getCourseById(courseId);

        if (course.getStatus() != ECourseStatus.UNDER_REVIEW) {
            throw new EduBadRequestException("Course must be UNDER_REVIEW to be approved.");
        }

        course.setStatus(ECourseStatus.APPROVED);
        course.setModerationRejectionReason(null);
        course.setUpdatedAt(Instant.now());

        logReview(courseId, reviewerId, "APPROVED", null);
        log.info("Course {} approved by reviewer {}", courseId, reviewerId);
        return courseRepository.save(course);
    }

    @Transactional
    public ECourses rejectCourse(String reviewerId, String courseId, String reason) {
        ECourses course = getCourseById(courseId);

        if (course.getStatus() != ECourseStatus.UNDER_REVIEW) {
            throw new EduBadRequestException("Course must be UNDER_REVIEW to be rejected.");
        }

        course.setStatus(ECourseStatus.REJECTED);
        course.setModerationRejectionReason(reason);
        course.setPublished(false);
        course.setUpdatedAt(Instant.now());

        logReview(courseId, reviewerId, "REJECTED", reason);
        log.info("Course {} rejected by reviewer {} for reason: {}", courseId, reviewerId, reason);
        return courseRepository.save(course);
    }

    @Transactional
    public ECourses publishCourse(String creatorId, String courseId) {
        accessGuard.enforceCourseOwnership(creatorId, courseId);
        ECourses course = getCourseById(courseId);

        if (course.getStatus() != ECourseStatus.APPROVED) {
            throw new EduBadRequestException("Course must be APPROVED before it can be published.");
        }

        course.setStatus(ECourseStatus.PUBLISHED);
        course.setPublished(true);
        course.setPublishedAt(Instant.now());
        course.setUpdatedAt(Instant.now());

        log.info("Course {} published by creator {}", courseId, creatorId);
        return courseRepository.save(course);
    }

    private ECourses getCourseById(String id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new EduResourceNotFoundException("Course not found with id: " + id));
    }

    private void logReview(String courseId, String reviewerId, String action, String reason) {
        EduCourseReviewLog log = EduCourseReviewLog.builder()
                .courseId(courseId)
                .reviewerId(reviewerId)
                .action(action)
                .reason(reason)
                .createdAt(Instant.now())
                .build();
        reviewLogRepository.save(log);
    }
}
