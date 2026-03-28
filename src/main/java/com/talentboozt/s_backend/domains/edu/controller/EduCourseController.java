package com.talentboozt.s_backend.domains.edu.controller;

import com.talentboozt.s_backend.domains.edu.dto.course.CourseRequest;
import com.talentboozt.s_backend.domains.edu.model.EEnrollments;
import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.service.EduCourseService;
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
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<ECourses> createCourse(
            @RequestParam String creatorId, 
            @RequestParam String workspaceId, 
            @RequestBody CourseRequest request) {
        return ResponseEntity.ok(courseService.createCourse(creatorId, workspaceId, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ECourses> getCourse(@PathVariable String id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @GetMapping("/creator/{creatorId}")
    public ResponseEntity<List<ECourses>> getCreatorCourses(@PathVariable String creatorId) {
        return ResponseEntity.ok(courseService.getCoursesByCreator(creatorId));
    }

    @GetMapping("/creator/{creatorId}/students")
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<List<EEnrollments>> getCreatorStudents(
            @PathVariable String creatorId,
            @RequestParam(required = false) String courseId,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(courseService.getCreatorStudentEnrollments(creatorId, courseId, search));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<ECourses> updateCourse(
            @PathVariable String id, 
            @RequestBody CourseRequest request) {
        return ResponseEntity.ok(courseService.updateCourse(id, request));
    }

    @PutMapping("/{id}/publish")
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<ECourses> publishCourse(@PathVariable String id) {
        return ResponseEntity.ok(courseService.publishCourse(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<Void> deleteCourse(@PathVariable String id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}
