package com.talentboozt.s_backend.domains.edu.controller;

import jakarta.validation.Valid;
import com.talentboozt.s_backend.domains.edu.dto.course.LessonRequest;
import com.talentboozt.s_backend.domains.edu.dto.course.OrderUpdateRequest;
import com.talentboozt.s_backend.domains.edu.model.ELessons;
import com.talentboozt.s_backend.domains.edu.service.EduLessonService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/edu/courses/{courseId}/sections/{sectionId}/lessons")
public class EduLessonController {

    private final EduLessonService lessonService;

    public EduLessonController(EduLessonService lessonService) {
        this.lessonService = lessonService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<ELessons> createLesson(
            @PathVariable String courseId,
            @PathVariable String sectionId,
            String creatorId,
            @Valid @RequestBody LessonRequest request) {
        return ResponseEntity.ok(lessonService.createLesson(courseId, sectionId, creatorId, request));
    }

    @GetMapping
    public ResponseEntity<List<ELessons>> getLessons(@PathVariable String sectionId) {
        return ResponseEntity.ok(lessonService.getLessonsBySectionId(sectionId));
    }

    @GetMapping("/{lessonId}")
    public ResponseEntity<ELessons> getLesson(@PathVariable String lessonId) {
        return ResponseEntity.ok(lessonService.getLessonById(lessonId));
    }

    @PutMapping("/{lessonId}")
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<ELessons> updateLesson(
            @PathVariable String lessonId, 
            @Valid @RequestBody LessonRequest request) {
        return ResponseEntity.ok(lessonService.updateLesson(lessonId, request));
    }

    @PutMapping("/reorder")
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<Void> reorderLessons(
            @PathVariable String sectionId, 
            @Valid @RequestBody OrderUpdateRequest request) {
        lessonService.reorderLessons(sectionId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{lessonId}")
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<Void> deleteLesson(@PathVariable String lessonId) {
        lessonService.deleteLesson(lessonId);
        return ResponseEntity.noContent().build();
    }
}
