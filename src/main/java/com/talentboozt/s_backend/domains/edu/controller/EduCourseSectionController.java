package com.talentboozt.s_backend.domains.edu.controller;

import jakarta.validation.Valid;
import com.talentboozt.s_backend.domains.edu.dto.course.OrderUpdateRequest;
import com.talentboozt.s_backend.domains.edu.dto.course.SectionRequest;
import com.talentboozt.s_backend.domains.edu.model.ECourseSections;
import com.talentboozt.s_backend.domains.edu.service.EduCourseSectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/edu/courses/{courseId}/sections")
public class EduCourseSectionController {

    private final EduCourseSectionService sectionService;

    public EduCourseSectionController(EduCourseSectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<ECourseSections> createSection(
            @PathVariable String courseId, 
            String creatorId,
            @Valid @RequestBody SectionRequest request) {
        return ResponseEntity.ok(sectionService.createSection(courseId, creatorId, request));
    }

    @GetMapping
    public ResponseEntity<List<ECourseSections>> getSections(@PathVariable String courseId) {
        return ResponseEntity.ok(sectionService.getSectionsByCourseId(courseId));
    }

    @PutMapping("/{sectionId}")
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<ECourseSections> updateSection(
            @PathVariable String sectionId, 
            @Valid @RequestBody SectionRequest request) {
        return ResponseEntity.ok(sectionService.updateSection(sectionId, request));
    }

    @PutMapping("/reorder")
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<Void> reorderSections(
            @PathVariable String courseId, 
            @Valid @RequestBody OrderUpdateRequest request) {
        sectionService.reorderSections(courseId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{sectionId}")
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<Void> deleteSection(@PathVariable String sectionId) {
        sectionService.deleteSection(sectionId);
        return ResponseEntity.noContent().build();
    }
}
