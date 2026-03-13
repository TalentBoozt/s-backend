package com.talentboozt.s_backend.domains.lifeplanner.seo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lifeplanner/public/seo")
public class PublicSEOController {

    @GetMapping("/cities")
    public ResponseEntity<List<Map<String, String>>> getFeaturedCities() {
        return ResponseEntity.ok(List.of(
            Map.of("id", "london", "name", "London", "country", "United Kingdom", "slug", "London"),
            Map.of("id", "tokyo", "name", "Tokyo", "country", "Japan", "slug", "Tokyo"),
            Map.of("id", "new-york", "name", "New York", "country", "United States", "slug", "New York"),
            Map.of("id", "san-francisco", "name", "San Francisco", "country", "United States", "slug", "San Francisco")
        ));
    }

    @GetMapping("/countries")
    public ResponseEntity<List<Map<String, String>>> getFeaturedCountries() {
        return ResponseEntity.ok(List.of(
            Map.of("id", "uk", "name", "United Kingdom", "slug", "United Kingdom"),
            Map.of("id", "usa", "name", "United States", "slug", "United States"),
            Map.of("id", "japan", "name", "Japan", "slug", "Japan")
        ));
    }

    @GetMapping("/dictionary")
    public ResponseEntity<List<Map<String, String>>> getDictionaryTerms() {
        return ResponseEntity.ok(List.of(
            Map.of("id", "deep-work", "term", "Deep Work", "slug", "Deep Work"),
            Map.of("id", "atomic-habits", "term", "Atomic Habits", "slug", "Atomic Habits"),
            Map.of("id", "flow-state", "term", "Flow State", "slug", "Flow State")
        ));
    }
}
