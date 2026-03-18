package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.dto.course.CourseRequest;
import com.talentboozt.s_backend.domains.edu.enums.ECourseStatus;
import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ECoursesRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class EduCourseService {

    private final ECoursesRepository courseRepository;

    public EduCourseService(ECoursesRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public ECourses createCourse(String creatorId, String workspaceId, CourseRequest request) {
        ECourses course = ECourses.builder()
                .creatorId(creatorId)
                .workspaceId(workspaceId)
                .title(request.getTitle())
                .description(request.getDescription())
                .shortDescription(request.getShortDescription())
                .type(request.getType())
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
        return courseRepository.save(course);
    }

    public ECourses getCourseById(String id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
    }

    public List<ECourses> getCoursesByCreator(String creatorId) {
        // Since we don't have custom robust queries in ECoursesRepository yet,
        // we'll just findAll and filter (temporary placeholder) or ideally, we'll write
        // the query.
        return courseRepository.findAll().stream()
                .filter(c -> creatorId.equals(c.getCreatorId()))
                .toList();
    }

    public ECourses updateCourse(String id, CourseRequest request) {
        ECourses course = getCourseById(id);
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setShortDescription(request.getShortDescription());
        course.setType(request.getType());
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

    public ECourses publishCourse(String id) {
        ECourses course = getCourseById(id);
        course.setStatus(ECourseStatus.PUBLISHED);
        course.setPublished(true);
        course.setPublishedAt(Instant.now());
        course.setUpdatedAt(Instant.now());
        return courseRepository.save(course);
    }

    public void deleteCourse(String id) {
        courseRepository.deleteById(id);
    }
}
