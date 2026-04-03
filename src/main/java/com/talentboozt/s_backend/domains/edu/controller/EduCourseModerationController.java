package com.talentboozt.s_backend.domains.edu.controller;

import com.talentboozt.s_backend.domains.edu.dto.moderation.CourseRejectRequest;
import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.service.EduCourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/edu/admin/moderation")
public class EduCourseModerationController {

    private final EduCourseService courseService;

    public EduCourseModerationController(EduCourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/courses/pending")
    @PreAuthorize("hasAuthority('PLATFORM_ADMIN')")
    public ResponseEntity<List<ECourses>> listPendingCourses() {
        return ResponseEntity.ok(courseService.listCoursesPendingModeration());
    }

    @PutMapping("/courses/{courseId}/approve")
    @PreAuthorize("hasAuthority('PLATFORM_ADMIN')")
    public ResponseEntity<ECourses> approveCourse(@PathVariable String courseId) {
        return ResponseEntity.ok(courseService.approveCourseForMarketplace(courseId));
    }

    @PutMapping("/courses/{courseId}/reject")
    @PreAuthorize("hasAuthority('PLATFORM_ADMIN')")
    public ResponseEntity<ECourses> rejectCourse(
            @PathVariable String courseId,
            @RequestBody(required = false) CourseRejectRequest body) {
        String reason = body != null ? body.getReason() : null;
        return ResponseEntity.ok(courseService.rejectCourseReview(courseId, reason));
    }
}
