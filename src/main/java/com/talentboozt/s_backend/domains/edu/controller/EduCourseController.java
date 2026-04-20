package com.talentboozt.s_backend.domains.edu.controller;

import jakarta.validation.Valid;
import com.talentboozt.s_backend.domains.edu.model.ECourseSections;
import com.talentboozt.s_backend.domains.edu.dto.course.CourseRequest;
import com.talentboozt.s_backend.domains.edu.model.EEnrollments;
import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.service.EduCourseService;
import com.talentboozt.s_backend.shared.security.annotations.AuthenticatedUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/edu/courses")
public class EduCourseController {

    private final EduCourseService courseService;

    public EduCourseController(EduCourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE')")
    public ResponseEntity<ECourses> createCourse(
            @AuthenticatedUser String creatorId, 
            @RequestParam String workspaceId, 
            @Valid @RequestBody CourseRequest request) {
        return ResponseEntity.ok(courseService.createCourse(creatorId, workspaceId, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ECourses> getCourse(@PathVariable String id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @GetMapping("/{id}/curriculum")
    public ResponseEntity<List<ECourseSections>> getCourseCurriculum(@PathVariable String id) {
        return ResponseEntity.ok(courseService.getFullCurriculum(id));
    }

    @GetMapping("/creator")
    @PreAuthorize("hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE')")
    public ResponseEntity<List<ECourses>> getCreatorCourses(@AuthenticatedUser String creatorId) {
        return ResponseEntity.ok(courseService.getCoursesByCreator(creatorId));
    }

    @GetMapping("/workspace/{workspaceId}")
    public ResponseEntity<List<ECourses>> getCoursesByWorkspace(@PathVariable String workspaceId) {
        return ResponseEntity.ok(courseService.getCoursesByWorkspace(workspaceId));
    }

    @GetMapping("/creator/students")
    @PreAuthorize("hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE')")
    public ResponseEntity<List<EEnrollments>> getCreatorStudents(
            @AuthenticatedUser String creatorId,
            @RequestParam(required = false) String courseId,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(courseService.getCreatorStudentEnrollments(creatorId, courseId, search));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE')")
    public ResponseEntity<ECourses> updateCourse(
            @AuthenticatedUser String creatorId,
            @PathVariable String id, 
            @Valid @RequestBody CourseRequest request) {
        return ResponseEntity.ok(courseService.updateCourse(creatorId, id, request));
    }

    @PutMapping("/{id}/publish")
    @PreAuthorize("hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE')")
    public ResponseEntity<ECourses> publishCourse(
            @AuthenticatedUser String creatorId,
            @PathVariable String id) {
        return ResponseEntity.ok(courseService.publishCourse(creatorId, id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE')")
    public ResponseEntity<Void> deleteCourse(
            @AuthenticatedUser String creatorId,
            @PathVariable String id) {
        courseService.deleteCourse(creatorId, id);
        return ResponseEntity.noContent().build();
    }
}
