package com.talentboozt.s_backend.domains.edu.controller;

import com.talentboozt.s_backend.domains.edu.dto.enrollment.EnrollmentRequest;
import com.talentboozt.s_backend.domains.edu.model.EEnrollments;
import com.talentboozt.s_backend.domains.edu.service.EduEnrollmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/edu/enrollments")
public class EduEnrollmentController {

    private final EduEnrollmentService enrollmentService;

    public EduEnrollmentController(EduEnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE')")
    public ResponseEntity<EEnrollments> enrollInCourse(
            @RequestParam String userId,
            @RequestBody EnrollmentRequest request) {
        return ResponseEntity.ok(enrollmentService.enrollInCourse(userId, request));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE')")
    public ResponseEntity<List<EEnrollments>> getUserEnrollments(@PathVariable String userId) {
        return ResponseEntity.ok(enrollmentService.getUserEnrollments(userId));
    }

    @GetMapping("/{enrollmentId}")
    @PreAuthorize("hasAuthority('LEARNER')")
    public ResponseEntity<EEnrollments> getEnrollmentDetails(@PathVariable String enrollmentId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentDetails(enrollmentId));
    }

    @PutMapping("/{enrollmentId}/progress")
    @PreAuthorize("hasAuthority('LEARNER')")
    public ResponseEntity<EEnrollments> recordProgress(
            @PathVariable String enrollmentId,
            @RequestParam String lessonId,
            @RequestParam(required = false) Long watchTime) {
        return ResponseEntity.ok(enrollmentService.recordProgress(enrollmentId, lessonId, watchTime));
    }

    @PutMapping("/{enrollmentId}/complete")
    @PreAuthorize("hasAuthority('LEARNER')")
    public ResponseEntity<EEnrollments> completeEnrollment(@PathVariable String enrollmentId) {
        return ResponseEntity.ok(enrollmentService.completeEnrollment(enrollmentId));
    }

    @GetMapping("/course/{courseId}/user/{userId}")
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE')")
    public ResponseEntity<EEnrollments> getCourseProgress(
            @PathVariable String courseId,
            @PathVariable String userId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentByCourseAndUser(courseId, userId));
    }
}
