package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.dto.course.CourseRequest;
import com.talentboozt.s_backend.domains.edu.enums.ECourseStatus;
import com.talentboozt.s_backend.domains.edu.enums.ECourseValidationStatus;
import com.talentboozt.s_backend.domains.edu.exception.EduBadRequestException;
import com.talentboozt.s_backend.domains.edu.exception.EduResourceNotFoundException;
import com.talentboozt.s_backend.domains.edu.model.EEnrollments;
import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ECoursesRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EEnrollmentsRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EWorkspacesRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class EduCourseService {

    private final ECoursesRepository courseRepository;
    private final EEnrollmentsRepository enrollmentsRepository;
    private final EWorkspacesRepository workspaceRepository;
    private final EduWorkspaceGuardService guardService;
    private final EduTrustScoreService trustScoreService;
    private final EduAccessGuardService accessGuard;
    private final com.talentboozt.s_backend.domains.edu.repository.mongodb.EProfilesRepository profilesRepository;
    private final com.talentboozt.s_backend.domains.edu.repository.mongodb.ECourseSectionsRepository sectionRepository;
    private final com.talentboozt.s_backend.domains.edu.repository.mongodb.ELessonsRepository lessonRepository;

    public EduCourseService(ECoursesRepository courseRepository, EEnrollmentsRepository enrollmentsRepository, EWorkspacesRepository workspaceRepository, EduWorkspaceGuardService guardService, EduTrustScoreService trustScoreService, EduAccessGuardService accessGuard, com.talentboozt.s_backend.domains.edu.repository.mongodb.EProfilesRepository profilesRepository, com.talentboozt.s_backend.domains.edu.repository.mongodb.ECourseSectionsRepository sectionRepository, com.talentboozt.s_backend.domains.edu.repository.mongodb.ELessonsRepository lessonRepository) {
        this.courseRepository = courseRepository;
        this.enrollmentsRepository = enrollmentsRepository;
        this.workspaceRepository = workspaceRepository;
        this.guardService = guardService;
        this.trustScoreService = trustScoreService;
        this.accessGuard = accessGuard;
        this.profilesRepository = profilesRepository;
        this.sectionRepository = sectionRepository;
        this.lessonRepository = lessonRepository;
    }

    public ECourses createCourse(String creatorId, String workspaceId, CourseRequest request) {
        accessGuard.enforceCourseCreationLimit(creatorId);
        
        if (workspaceId != null && !workspaceId.isEmpty() && !"default".equals(workspaceId)) {
            guardService.enforceMembership(workspaceId, creatorId);
        }
        ECourses course = ECourses.builder()
                .creatorId(creatorId)
                .workspaceId(workspaceId)
                .title(request.getTitle())
                .description(request.getDescription())
                .shortDescription(request.getShortDescription())
                .thumbnail(request.getThumbnail())
                .previewVideoUrl(request.getPreviewVideoUrl())
                .type(request.getType())
                .contentType(request.getContentType())
                .language(request.getLanguage() != null ? request.getLanguage() : "en")
                .level(request.getLevel())
                .tags(request.getTags() != null ? request.getTags() : new String[0])
                .categories(request.getCategories())
                .subCategories(request.getSubCategories() != null ? request.getSubCategories() : new String[0])
                .keywords(request.getKeywords() != null ? request.getKeywords() : new String[0])
                .skills(request.getSkills() != null ? request.getSkills() : new String[0])
                .prerequisites(request.getPrerequisites() != null ? request.getPrerequisites() : new String[0])
                .outcomes(request.getOutcomes() != null ? request.getOutcomes() : new String[0])
                .price(request.getPrice() != null ? request.getPrice() : 0.0)
                .compareAtPrice(request.getCompareAtPrice())
                .currency(request.getCurrency() != null ? request.getCurrency() : "USD")
                .isPrivate(request.getIsPrivate() != null ? request.getIsPrivate() : false)
                .status(ECourseStatus.DRAFT)
                .published(false)
                .totalEnrollments(0)
                .totalReviews(0)
                .totalHours(0)
                .totalLessons(0)
                .rating(0.0)
                .slug(generateSlug(request.getTitle()))
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
                .orElseThrow(() -> new EduResourceNotFoundException("Course not found with id: " + id));
        guardService.enforceResourceIsolation(course.getWorkspaceId());
        populateTrustData(course);
        return course;
    }

    public List<ECourses> getCoursesByCreator(String creatorId) {
        List<ECourses> courses = courseRepository.findByCreatorId(creatorId);
        
        com.talentboozt.s_backend.shared.tenant.TenantContext ctx = com.talentboozt.s_backend.shared.tenant.TenantContext.getCurrent();
        if (ctx != null && ctx.getWorkspaceId() != null && !ctx.getWorkspaceId().isEmpty() && !"default".equals(ctx.getWorkspaceId())) {
            courses = courses.stream()
                .filter(c -> ctx.getWorkspaceId().equals(c.getWorkspaceId()))
                .collect(Collectors.toList());
        }

        courses.forEach(this::populateTrustData);
        return courses;
    }

    public List<ECourses> getCoursesByWorkspace(String workspaceId) {
        guardService.enforceCurrentContext(null); // Uses TenantContext
        List<ECourses> courses = courseRepository.findByWorkspaceId(workspaceId);
        courses.forEach(this::populateTrustData);
        return courses;
    }

    public ECourses updateCourse(String creatorId, String id, CourseRequest request) {
        accessGuard.enforceCourseOwnership(creatorId, id);
        ECourses course = getCourseById(id);
        if (request.getTitle() != null && !request.getTitle().equals(course.getTitle())) {
            course.setTitle(request.getTitle());
            course.setSlug(generateSlug(request.getTitle()));
        } else if (course.getSlug() == null || course.getSlug().isEmpty()) {
            course.setSlug(generateSlug(course.getTitle()));
        }
        course.setDescription(request.getDescription());
        course.setShortDescription(request.getShortDescription());
        course.setThumbnail(request.getThumbnail());
        course.setPreviewVideoUrl(request.getPreviewVideoUrl());
        course.setType(request.getType());
        course.setContentType(request.getContentType());
        course.setLanguage(request.getLanguage());
        course.setLevel(request.getLevel());
        course.setTags(request.getTags());
        course.setCategories(request.getCategories());
        course.setSubCategories(request.getSubCategories());
        course.setKeywords(request.getKeywords());
        course.setSkills(request.getSkills());
        course.setPrerequisites(request.getPrerequisites());
        course.setOutcomes(request.getOutcomes());
        course.setPrice(request.getPrice());
        course.setCompareAtPrice(request.getCompareAtPrice());
        if (request.getCurrency() != null)
            course.setCurrency(request.getCurrency());
        if (request.getIsPrivate() != null)
            course.setIsPrivate(request.getIsPrivate());
        if (request.getIsFeatured() != null)
            course.setIsFeatured(request.getIsFeatured());
        if (request.getIsTrending() != null)
            course.setIsTrending(request.getIsTrending());
        if (request.getSearchRank() != null)
            course.setSearchRank(request.getSearchRank());
        course.setUpdatedAt(Instant.now());
        return courseRepository.save(course);
    }

    /**
     * Creator submits course for platform moderation. Not visible in marketplace until approved.
     */
    public ECourses publishCourse(String creatorId, String id) {
        accessGuard.enforceCourseOwnership(creatorId, id);
        ECourses course = getCourseById(id);
        course.setStatus(ECourseStatus.PUBLISHED);
        course.setPublished(true);
        course.setPublishedAt(Instant.now());
        course.setModerationRejectionReason(null);
        course.setUpdatedAt(Instant.now());
        return courseRepository.save(course);
    }

    public List<ECourses> listCoursesPendingModeration() {
        return courseRepository.findByStatus(ECourseStatus.PENDING_REVIEW);
    }

    public ECourses approveCourseForMarketplace(String courseId) {
        ECourses course = getCourseById(courseId);
        if (course.getValidationStatus() != ECourseValidationStatus.MANUAL_PENDING) {
            throw new EduBadRequestException("Course is not awaiting manual validation");
        }
        course.setStatus(ECourseStatus.PUBLISHED);
        course.setPublished(true);
        course.setPublishedAt(Instant.now());
        course.setTalnovaVerified(true);
        course.setValidationStatus(ECourseValidationStatus.VALIDATED);
        course.setModerationRejectionReason(null);
        course.setUpdatedAt(Instant.now());
        return courseRepository.save(course);
    }

    public ECourses rejectCourseReview(String courseId, String reason) {
        ECourses course = getCourseById(courseId);
        if (course.getStatus() != ECourseStatus.PENDING_REVIEW) {
            throw new EduBadRequestException("Course is not awaiting moderation");
        }
        course.setStatus(ECourseStatus.DRAFT);
        course.setPublished(false);
        course.setModerationRejectionReason(reason != null ? reason : "");
        course.setUpdatedAt(Instant.now());
        return courseRepository.save(course);
    }

    public void deleteCourse(String creatorId, String id) {
        accessGuard.enforceCourseOwnership(creatorId, id);
        courseRepository.deleteById(id);
    }

    /**
     * All enrollments for courses owned by a creator (optional filter by course and user id text).
     */
    public List<EEnrollments> getCreatorStudentEnrollments(String creatorId, String courseId, String search) {
        com.talentboozt.s_backend.shared.tenant.TenantContext ctx = com.talentboozt.s_backend.shared.tenant.TenantContext.getCurrent();
        
        List<ECourses> owned = courseRepository.findAll().stream()
                .filter(c -> creatorId.equals(c.getCreatorId()))
                .filter(c -> courseId == null || courseId.isEmpty() || c.getId().equals(courseId))
                .filter(c -> {
                    if (ctx == null || ctx.getWorkspaceId() == null || ctx.getWorkspaceId().isEmpty() || "default".equals(ctx.getWorkspaceId())) {
                        return true;
                    }
                    return ctx.getWorkspaceId().equals(c.getWorkspaceId());
                })
                .toList();
        String needle = search != null ? search.trim().toLowerCase(Locale.ROOT) : "";
        Stream<EEnrollments> stream = owned.stream()
                .flatMap(c -> enrollmentsRepository.findByCourseId(c.getId()).stream());
        if (!needle.isEmpty()) {
            stream = stream.filter(e -> e.getUserId() != null && e.getUserId().toLowerCase(Locale.ROOT).contains(needle));
        }
        
        List<EEnrollments> results = stream.collect(Collectors.toList());
        for (EEnrollments e : results) {
            // Default fallbacks
            e.setName("Learner");
            
            if (e.getCourseId() != null) {
                courseRepository.findById(e.getCourseId()).ifPresent(e::setCourse);
            }
            if (e.getUserId() != null) {
                profilesRepository.findByUserId(e.getUserId()).ifPresent(p -> {
                    String name = "";
                    if (p.getFirstName() != null) name += p.getFirstName();
                    if (p.getLastName() != null) name += (name.isEmpty() ? "" : " ") + p.getLastName();
                    if (!name.isEmpty()) e.setName(name);
                    e.setEmail(p.getPublicEmail());
                    e.setAvatar(p.getAvatarUrl());
                });
                int count = enrollmentsRepository.findByUserId(e.getUserId()).size();
                e.setCoursesCount(count);
            }
        }

        // Apply search filter on populated data if needed
        if (!needle.isEmpty()) {
            results = results.stream()
                .filter(e -> (e.getName() != null && e.getName().toLowerCase(Locale.ROOT).contains(needle)) ||
                             (e.getEmail() != null && e.getEmail().toLowerCase(Locale.ROOT).contains(needle)) ||
                             (e.getUserId() != null && e.getUserId().toLowerCase(Locale.ROOT).contains(needle)))
                .collect(Collectors.toList());
        }

        return results;
    }
    public List<com.talentboozt.s_backend.domains.edu.model.ECourseSections> getFullCurriculum(String courseId) {
        List<com.talentboozt.s_backend.domains.edu.model.ECourseSections> sections = sectionRepository.findByCourseId(courseId);
        List<com.talentboozt.s_backend.domains.edu.model.ELessons> lessons = lessonRepository.findByCourseId(courseId);

        // Group lessons by sectionId for efficient population
        java.util.Map<String, List<com.talentboozt.s_backend.domains.edu.model.ELessons>> lessonMap = lessons.stream()
                .collect(Collectors.groupingBy(com.talentboozt.s_backend.domains.edu.model.ELessons::getSectionId));

        for (com.talentboozt.s_backend.domains.edu.model.ECourseSections section : sections) {
            section.setLessonDetails(lessonMap.getOrDefault(section.getId(), java.util.Collections.emptyList())
                    .stream()
                    .sorted(java.util.Comparator.comparingInt(com.talentboozt.s_backend.domains.edu.model.ELessons::getOrder))
                    .collect(Collectors.toList()));
        }

        return sections.stream()
                .sorted(java.util.Comparator.comparingInt(com.talentboozt.s_backend.domains.edu.model.ECourseSections::getOrder))
                .collect(Collectors.toList());
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

    private String generateSlug(String title) {
        if (title == null || title.isEmpty()) {
            return "course-" + UUID.randomUUID().toString().substring(0, 8);
        }
        String normalized = title.toLowerCase(Locale.ROOT);
        String slug = Pattern.compile("[^a-z0-9]").matcher(normalized).replaceAll("-");
        slug = Pattern.compile("-+").matcher(slug).replaceAll("-");
        // Remove trailing hyphens
        if (slug.endsWith("-")) {
            slug = slug.substring(0, slug.length() - 1);
        }
        // Ensure uniqueness (simple append, ideally check in DB if needed but short UUID is safer)
        return slug + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
