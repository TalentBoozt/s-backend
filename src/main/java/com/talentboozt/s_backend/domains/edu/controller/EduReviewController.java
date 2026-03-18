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

    public EduReviewController(EduReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/course/{courseId}")
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('INSTRUCTOR')")
    public ResponseEntity<EReviews> addReview(
            @PathVariable String courseId,
            @RequestParam String userId,
            @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.addReview(courseId, userId, request));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<EReviews>> getCourseReviews(@PathVariable String courseId) {
        return ResponseEntity.ok(reviewService.getCourseReviews(courseId));
    }

    @PutMapping("/{reviewId}")
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('INSTRUCTOR')")
    public ResponseEntity<EReviews> updateReview(
            @PathVariable String reviewId,
            @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.updateReview(reviewId, request));
    }

    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteReview(@PathVariable String reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}
