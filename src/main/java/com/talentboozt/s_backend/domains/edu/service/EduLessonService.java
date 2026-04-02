package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.dto.course.LessonRequest;
import com.talentboozt.s_backend.domains.edu.dto.course.OrderUpdateRequest;
import com.talentboozt.s_backend.domains.edu.exception.EduResourceNotFoundException;
import com.talentboozt.s_backend.domains.edu.model.ECourseSections;
import com.talentboozt.s_backend.domains.edu.model.ELessons;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ECourseSectionsRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ELessonsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class EduLessonService {

    private final ELessonsRepository lessonRepository;
    private final ECourseSectionsRepository sectionRepository;

    public EduLessonService(ELessonsRepository lessonRepository, ECourseSectionsRepository sectionRepository) {
        this.lessonRepository = lessonRepository;
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public ELessons createLesson(String courseId, String sectionId, String creatorId, LessonRequest request) {
        ECourseSections section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new EduResourceNotFoundException("Section not found with id: " + sectionId));

        ELessons lesson = ELessons.builder()
                .courseId(courseId)
                .sectionId(sectionId)
                .title(request.getTitle())
                .description(request.getDescription())
                .type(request.getType())
                .contentUrl(request.getContentUrl())
                .textContent(request.getTextContent())
                .markdownContent(request.getMarkdownContent())
                .duration(request.getDuration() != null ? request.getDuration() : 0)
                .isFreePreview(request.getIsFreePreview() != null ? request.getIsFreePreview() : false)
                .order(request.getOrder() != null ? request.getOrder() : getNextOrder(sectionId))
                .videoThumbnail(request.getVideoThumbnail())
                .attachments(request.getAttachments())
                .isPublished(true)
                .createdBy(creatorId)
                .createdAt(Instant.now())
                .build();

        ELessons saved = lessonRepository.save(lesson);

        List<String> lessonsList = new ArrayList<>(
                Arrays.asList(section.getLessons() != null ? section.getLessons() : new String[0]));
        lessonsList.add(saved.getId());
        section.setLessons(lessonsList.toArray(new String[0]));
        sectionRepository.save(section);

        return saved;
    }

    public ELessons getLessonById(String id) {
        return lessonRepository.findById(id).orElseThrow(() -> new EduResourceNotFoundException("Lesson not found with id: " + id));
    }

    public List<ELessons> getLessonsBySectionId(String sectionId) {
        return lessonRepository.findBySectionId(sectionId);
    }

    public ELessons updateLesson(String id, LessonRequest request) {
        ELessons lesson = getLessonById(id);
        if (request.getTitle() != null)
            lesson.setTitle(request.getTitle());
        if (request.getDescription() != null)
            lesson.setDescription(request.getDescription());
        if (request.getType() != null)
            lesson.setType(request.getType());
        if (request.getContentUrl() != null)
            lesson.setContentUrl(request.getContentUrl());
        if (request.getTextContent() != null)
            lesson.setTextContent(request.getTextContent());
        if (request.getMarkdownContent() != null)
            lesson.setMarkdownContent(request.getMarkdownContent());
        if (request.getDuration() != null)
            lesson.setDuration(request.getDuration());
        if (request.getIsFreePreview() != null)
            lesson.setIsFreePreview(request.getIsFreePreview());
        if (request.getVideoThumbnail() != null)
            lesson.setVideoThumbnail(request.getVideoThumbnail());
        if (request.getAttachments() != null)
            lesson.setAttachments(request.getAttachments());
        lesson.setUpdatedAt(Instant.now());

        return lessonRepository.save(lesson);
    }

    @Transactional
    public void reorderLessons(String sectionId, OrderUpdateRequest request) {
        ECourseSections section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new EduResourceNotFoundException("Section not found with id: " + sectionId));

        section.setLessons(request.getOrderedIds().toArray(new String[0]));
        sectionRepository.save(section);

        int order = 1;
        for (String id : request.getOrderedIds()) {
            ELessons lesson = getLessonById(id);
            lesson.setOrder(order++);
            lessonRepository.save(lesson);
        }
    }

    public void deleteLesson(String id) {
        ELessons lesson = getLessonById(id);
        ECourseSections section = sectionRepository.findById(lesson.getSectionId()).orElse(null);
        if (section != null && section.getLessons() != null) {
            List<String> list = new ArrayList<>(Arrays.asList(section.getLessons()));
            list.remove(id);
            section.setLessons(list.toArray(new String[0]));
            sectionRepository.save(section);
        }
        lessonRepository.deleteById(id);
    }

    private int getNextOrder(String sectionId) {
        return getLessonsBySectionId(sectionId).size() + 1;
    }
}
