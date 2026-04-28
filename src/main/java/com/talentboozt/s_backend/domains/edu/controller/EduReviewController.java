package com.talentboozt.s_backend.domains.edu.controller;

import com.talentboozt.s_backend.domains.edu.dto.engagement.ReviewRequest;
import com.talentboozt.s_backend.domains.edu.model.EReviews;
import com.talentboozt.s_backend.domains.edu.service.EduReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/edu/reviews")
public class EduReviewController {

    private final EduReviewService reviewService;
    private final com.talentboozt.s_backend.shared.security.utils.SecurityUtils securityUtils;

    public EduReviewController(EduReviewService reviewService,
                               com.talentboozt.s_backend.shared.security.utils.SecurityUtils securityUtils) {
        this.reviewService = reviewService;
        this.securityUtils = securityUtils;
    }

    @PostMapping("/course/{courseId}")
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('ENTERPRISE_INSTRUCTOR')")
    public ResponseEntity<EReviews> addReview(
            @PathVariable String courseId,
            @RequestBody ReviewRequest request) {
        String userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(reviewService.addReview(courseId, userId, request));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<EReviews>> getCourseReviews(@PathVariable String courseId) {
        return ResponseEntity.ok(reviewService.getCourseReviews(courseId));
    }

    @PutMapping("/{reviewId}")
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('ENTERPRISE_INSTRUCTOR')")
    public ResponseEntity<EReviews> updateReview(
            @PathVariable String reviewId,
            @RequestBody ReviewRequest request) {
        String userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(reviewService.updateReview(reviewId, userId, false, request));
    }

    @DeleteMapping("/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteReview(@PathVariable String reviewId) {
        String userId = securityUtils.getCurrentUserId();
        boolean isAdmin = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("PLATFORM_ADMIN"));
        
        reviewService.deleteReview(reviewId, userId, isAdmin);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{reviewId}/report")
    public ResponseEntity<Void> reportReview(@PathVariable String reviewId) {
        reviewService.reportReview(reviewId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{reviewId}/visibility")
    @PreAuthorize("hasAuthority('PLATFORM_ADMIN')")
    public ResponseEntity<Void> toggleVisibility(@PathVariable String reviewId, @RequestParam boolean isVisible) {
        reviewService.toggleVisibility(reviewId, isVisible);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{reviewId}/helpful")
    public ResponseEntity<EReviews> markReviewHelpful(@PathVariable String reviewId) {
        return ResponseEntity.ok(reviewService.incrementHelpful(reviewId));
    }
}
