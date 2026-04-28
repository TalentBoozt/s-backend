package com.talentboozt.s_backend.domains.edu.controller;

import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.service.EduRecommendationService;
import com.talentboozt.s_backend.shared.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/edu/recommendations")
@RequiredArgsConstructor
public class EduRecommendationController {

    private final EduRecommendationService recommendationService;
    private final SecurityUtils securityUtils;

    @GetMapping("/trending")
    public ResponseEntity<List<ECourses>> getTrending() {
        return ResponseEntity.ok(recommendationService.getTrendingCourses());
    }

    @GetMapping("/personalized")
    public ResponseEntity<List<ECourses>> getPersonalized() {
        String userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(recommendationService.getRecommendedCourses(userId));
    }

    @GetMapping("/similar/{courseId}")
    public ResponseEntity<List<ECourses>> getSimilar(@PathVariable String courseId) {
        return ResponseEntity.ok(recommendationService.getSimilarCourses(courseId));
    }
}
