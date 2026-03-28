package com.talentboozt.s_backend.domains.edu.controller;

import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.service.EduMarketplaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/edu/marketplace")
public class EduMarketplaceController {

    private final EduMarketplaceService marketplaceService;

    public EduMarketplaceController(EduMarketplaceService marketplaceService) {
        this.marketplaceService = marketplaceService;
    }

    @GetMapping("/featured")
    public ResponseEntity<List<ECourses>> getFeaturedCourses() {
        return ResponseEntity.ok(marketplaceService.getFeaturedCourses());
    }

    @GetMapping("/search")
    public ResponseEntity<List<ECourses>> searchCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) Double priceMax) {
        return ResponseEntity.ok(marketplaceService.searchCourses(keyword, category, level, priceMax));
    }

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<ECourses> getCourseDetails(@PathVariable String courseId) {
        return ResponseEntity.ok(marketplaceService.getCourseDetails(courseId));
    }
}
