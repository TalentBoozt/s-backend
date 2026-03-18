package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.dto.course.OrderUpdateRequest;
import com.talentboozt.s_backend.domains.edu.dto.course.SectionRequest;
import com.talentboozt.s_backend.domains.edu.model.ECourseSections;
import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ECourseSectionsRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ECoursesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class EduCourseSectionService {

    private final ECourseSectionsRepository sectionRepository;
    private final ECoursesRepository courseRepository;

    public EduCourseSectionService(ECourseSectionsRepository sectionRepository, ECoursesRepository courseRepository) {
        this.sectionRepository = sectionRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public ECourseSections createSection(String courseId, String creatorId, SectionRequest request) {
        ECourses course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        ECourseSections section = ECourseSections.builder()
                .courseId(courseId)
                .title(request.getTitle())
                .description(request.getDescription())
                .order(request.getOrder() != null ? request.getOrder() : getNextOrder(courseId))
                .lessons(new String[0])
                .createdBy(creatorId)
                .createdAt(Instant.now())
                .build();

        ECourseSections saved = sectionRepository.save(section);

        // Update Course parent
        List<String> sectionsList = new ArrayList<>(
                Arrays.asList(course.getSections() != null ? course.getSections() : new String[0]));
        sectionsList.add(saved.getId());
        course.setSections(sectionsList.toArray(new String[0]));
        courseRepository.save(course);

        return saved;
    }

    public ECourseSections getSectionById(String sectionId) {
        return sectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Section not found"));
    }

    public List<ECourseSections> getSectionsByCourseId(String courseId) {
        return sectionRepository.findAll().stream()
                .filter(s -> courseId.equals(s.getCourseId()))
                .toList(); // Note: placeholder until we write custom repo methods
    }

    public ECourseSections updateSection(String sectionId, SectionRequest request) {
        ECourseSections section = getSectionById(sectionId);
        if (request.getTitle() != null)
            section.setTitle(request.getTitle());
        if (request.getDescription() != null)
            section.setDescription(request.getDescription());
        section.setUpdatedAt(Instant.now());
        return sectionRepository.save(section);
    }

    @Transactional
    public void reorderSections(String courseId, OrderUpdateRequest request) {
        ECourses course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        course.setSections(request.getOrderedIds().toArray(new String[0]));
        courseRepository.save(course);

        // Also update the logical 'order' field on each section
        int order = 1;
        for (String id : request.getOrderedIds()) {
            ECourseSections section = getSectionById(id);
            section.setOrder(order++);
            sectionRepository.save(section);
        }
    }

    public void deleteSection(String id) {
        // Find course and remove from array
        ECourseSections section = getSectionById(id);
        ECourses course = courseRepository.findById(section.getCourseId()).orElse(null);
        if (course != null && course.getSections() != null) {
            List<String> sectionsList = new ArrayList<>(Arrays.asList(course.getSections()));
            sectionsList.remove(id);
            course.setSections(sectionsList.toArray(new String[0]));
            courseRepository.save(course);
        }

        sectionRepository.deleteById(id);
    }

    private int getNextOrder(String courseId) {
        return getSectionsByCourseId(courseId).size() + 1;
    }
}
