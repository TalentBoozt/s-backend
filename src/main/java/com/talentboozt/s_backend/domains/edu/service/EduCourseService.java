package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.dto.course.CourseRequest;
import com.talentboozt.s_backend.domains.edu.enums.ECourseStatus;
import com.talentboozt.s_backend.domains.edu.model.EEnrollments;
import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ECoursesRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EEnrollmentsRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EWorkspacesRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class EduCourseService {

    private final ECoursesRepository courseRepository;
    private final EEnrollmentsRepository enrollmentsRepository;
    private final EWorkspacesRepository workspaceRepository;
    private final EduWorkspaceGuardService guardService;
    private final EduTrustScoreService trustScoreService;

    public EduCourseService(ECoursesRepository courseRepository, EEnrollmentsRepository enrollmentsRepository, EWorkspacesRepository workspaceRepository, EduWorkspaceGuardService guardService, EduTrustScoreService trustScoreService) {
        this.courseRepository = courseRepository;
        this.enrollmentsRepository = enrollmentsRepository;
        this.workspaceRepository = workspaceRepository;
        this.guardService = guardService;
        this.trustScoreService = trustScoreService;
    }

    public ECourses createCourse(String creatorId, String workspaceId, CourseRequest request) {
        if (workspaceId != null && !workspaceId.isEmpty() && !"default".equals(workspaceId)) {
            guardService.enforceMembership(workspaceId, creatorId);
        }
        ECourses course = ECourses.builder()
                .creatorId(creatorId)
                .workspaceId(workspaceId)
                .title(request.getTitle())
                .description(request.getDescription())
                .shortDescription(request.getShortDescription())
                .type(request.getType())
                .contentType(request.getContentType())
                .language(request.getLanguage())
                .level(request.getLevel())
                .categories(request.getCategories())
                .subCategories(request.getSubCategories())
                .price(request.getPrice())
                .currency(request.getCurrency() != null ? request.getCurrency() : "USD")
                .isPrivate(request.getIsPrivate() != null ? request.getIsPrivate() : false)
                .status(ECourseStatus.DRAFT)
                .published(false)
                .totalEnrollments(0)
                .rating(0.0)
                .sections(new String[0])
                .createdAt(Instant.now())
                .build();
                
        ECourses saved = courseRepository.save(course);
        
        if (workspaceId != null && !workspaceId.isEmpty() && !"default".equals(workspaceId)) {
            workspaceRepository.findById(workspaceId).ifPresent(ws -> {
                ws.setTotalCourses(ws.getTotalCourses() + 1);
                workspaceRepository.save(ws);
            });
        }
        
        return saved;
    }

    public ECourses getCourseById(String id) {
        ECourses course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        populateTrustData(course);
        return course;
    }

    public List<ECourses> getCoursesByCreator(String creatorId) {
        List<ECourses> courses = courseRepository.findByCreatorId(creatorId);
        courses.forEach(this::populateTrustData);
        return courses;
    }

    public List<ECourses> getCoursesByWorkspace(String workspaceId) {
        guardService.enforceCurrentContext(null); // Uses TenantContext
        List<ECourses> courses = courseRepository.findByWorkspaceId(workspaceId);
        courses.forEach(this::populateTrustData);
        return courses;
    }

    public ECourses updateCourse(String id, CourseRequest request) {
        ECourses course = getCourseById(id);
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setShortDescription(request.getShortDescription());
        course.setType(request.getType());
        course.setContentType(request.getContentType());
        course.setLanguage(request.getLanguage());
        course.setLevel(request.getLevel());
        course.setCategories(request.getCategories());
        course.setSubCategories(request.getSubCategories());
        course.setPrice(request.getPrice());
        if (request.getCurrency() != null)
            course.setCurrency(request.getCurrency());
        if (request.getIsPrivate() != null)
            course.setIsPrivate(request.getIsPrivate());
        course.setUpdatedAt(Instant.now());
        return courseRepository.save(course);
    }

    /**
     * Creator submits course for platform moderation. Not visible in marketplace until approved.
     */
    public ECourses publishCourse(String id) {
        ECourses course = getCourseById(id);
        course.setStatus(ECourseStatus.PENDING_REVIEW);
        course.setPublished(false);
        course.setModerationRejectionReason(null);
        course.setUpdatedAt(Instant.now());
        return courseRepository.save(course);
    }

    public List<ECourses> listCoursesPendingModeration() {
        return courseRepository.findByStatus(ECourseStatus.PENDING_REVIEW);
    }

    public ECourses approveCourseForMarketplace(String courseId) {
        ECourses course = getCourseById(courseId);
        if (course.getStatus() != ECourseStatus.PENDING_REVIEW) {
            throw new RuntimeException("Course is not awaiting moderation");
        }
        course.setStatus(ECourseStatus.PUBLISHED);
        course.setPublished(true);
        course.setPublishedAt(Instant.now());
        course.setModerationRejectionReason(null);
        course.setUpdatedAt(Instant.now());
        return courseRepository.save(course);
    }

    public ECourses rejectCourseReview(String courseId, String reason) {
        ECourses course = getCourseById(courseId);
        if (course.getStatus() != ECourseStatus.PENDING_REVIEW) {
            throw new RuntimeException("Course is not awaiting moderation");
        }
        course.setStatus(ECourseStatus.DRAFT);
        course.setPublished(false);
        course.setModerationRejectionReason(reason != null ? reason : "");
        course.setUpdatedAt(Instant.now());
        return courseRepository.save(course);
    }

    public void deleteCourse(String id) {
        courseRepository.deleteById(id);
    }

    /**
     * All enrollments for courses owned by a creator (optional filter by course and user id text).
     */
    public List<EEnrollments> getCreatorStudentEnrollments(String creatorId, String courseId, String search) {
        List<ECourses> owned = courseRepository.findAll().stream()
                .filter(c -> creatorId.equals(c.getCreatorId()))
                .filter(c -> courseId == null || courseId.isEmpty() || c.getId().equals(courseId))
                .toList();
        String needle = search != null ? search.trim().toLowerCase(Locale.ROOT) : "";
        Stream<EEnrollments> stream = owned.stream()
                .flatMap(c -> enrollmentsRepository.findByCourseId(c.getId()).stream());
        if (!needle.isEmpty()) {
            stream = stream.filter(e -> e.getUserId() != null && e.getUserId().toLowerCase(Locale.ROOT).contains(needle));
        }
        return stream.collect(Collectors.toList());
    }
    private void populateTrustData(ECourses course) {
        if (course.getCreatorId() != null) {
            var trust = trustScoreService.getTrustScore(course.getCreatorId());
            course.setCreatorTier(trust.getCurrentTier());
            if ("BRONZE".equals(trust.getCurrentTier())) {
                course.setTrustWarning("Marketplace Warning: This creator has a low trust score. Exercise caution.");
            }
        }
    }
}
