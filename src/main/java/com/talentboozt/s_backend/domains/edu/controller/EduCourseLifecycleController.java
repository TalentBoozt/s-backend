package com.talentboozt.s_backend.domains.edu.controller;

import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.service.EduCourseLifecycleService;
import com.talentboozt.s_backend.shared.security.annotations.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/edu/courses/{id}")
@RequiredArgsConstructor
public class EduCourseLifecycleController {

    private final EduCourseLifecycleService lifecycleService;

    @PostMapping("/submit")
    @PreAuthorize("hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE') or hasAuthority('SELLER_PRO') or hasAuthority('SELLER_PREMIUM')")
    public ResponseEntity<ECourses> submitCourse(
            @AuthenticatedUser String userId,
            @PathVariable String id) {
        return ResponseEntity.ok(lifecycleService.submitCourse(userId, id));
    }

    @PostMapping("/start-review")
    @PreAuthorize("hasAuthority('REVIEWER') or hasAuthority('PLATFORM_ADMIN')")
    public ResponseEntity<ECourses> startReview(
            @AuthenticatedUser String reviewerId,
            @PathVariable String id) {
        return ResponseEntity.ok(lifecycleService.startReview(reviewerId, id));
    }

    @PostMapping("/approve")
    @PreAuthorize("hasAuthority('REVIEWER') or hasAuthority('PLATFORM_ADMIN')")
    public ResponseEntity<ECourses> approveCourse(
            @AuthenticatedUser String reviewerId,
            @PathVariable String id) {
        return ResponseEntity.ok(lifecycleService.approveCourse(reviewerId, id));
    }

    @PostMapping("/reject")
    @PreAuthorize("hasAuthority('REVIEWER') or hasAuthority('PLATFORM_ADMIN')")
    public ResponseEntity<ECourses> rejectCourse(
            @AuthenticatedUser String reviewerId,
            @PathVariable String id,
            @RequestParam String reason) {
        return ResponseEntity.ok(lifecycleService.rejectCourse(reviewerId, id, reason));
    }

    @PostMapping("/publish")
    @PreAuthorize("hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE') or hasAuthority('SELLER_PRO') or hasAuthority('SELLER_PREMIUM')")
    public ResponseEntity<ECourses> publishCourse(
            @AuthenticatedUser String userId,
            @PathVariable String id) {
        return ResponseEntity.ok(lifecycleService.publishCourse(userId, id));
    }
}
