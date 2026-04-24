package com.talentboozt.s_backend.domains.edu.controller;

import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.service.EduMarketplaceService;
import com.talentboozt.s_backend.domains.edu.service.EduPersonalizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/edu/marketplace")
public class EduMarketplaceController {

    private final EduMarketplaceService marketplaceService;
    private final EduPersonalizationService personalizationService;

    public EduMarketplaceController(EduMarketplaceService marketplaceService,
            EduPersonalizationService personalizationService) {
        this.marketplaceService = marketplaceService;
        this.personalizationService = personalizationService;
    }

    @GetMapping("/featured")
    public ResponseEntity<List<ECourses>> getFeaturedCourses() {
        return ResponseEntity.ok(marketplaceService.getFeaturedCourses());
    }

    @GetMapping("/search")
    public ResponseEntity<org.springframework.data.domain.Page<ECourses>> searchCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double priceMax,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Double upper = maxPrice != null ? maxPrice : priceMax;
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        return ResponseEntity.ok(marketplaceService.searchCourses(keyword, category, level, minPrice, upper, pageable));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(marketplaceService.getDistinctCategories());
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<ECourses>> getRecommendations(@RequestParam(required = false) String userId) {
        if (userId == null || userId.isBlank()) {
            return ResponseEntity.ok(marketplaceService.getFeaturedCourses());
        }
        return ResponseEntity.ok(personalizationService.getRecommendations(userId));
    }

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<ECourses> getCourseDetails(@PathVariable String courseId) {
        return ResponseEntity.ok(marketplaceService.getCourseDetails(courseId));
    }

    @GetMapping("/instructors")
    public ResponseEntity<List<com.talentboozt.s_backend.domains.edu.model.EProfiles>> getTopInstructors() {
        return ResponseEntity.ok(marketplaceService.getTopInstructors());
    }

    @GetMapping("/courses/creator/{creatorId}")
    public ResponseEntity<List<ECourses>> getCoursesByCreator(@PathVariable String creatorId) {
        return ResponseEntity.ok(marketplaceService.getCoursesByCreator(creatorId));
    }
}
