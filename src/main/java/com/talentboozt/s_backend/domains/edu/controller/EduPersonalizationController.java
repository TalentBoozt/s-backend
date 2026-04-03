package com.talentboozt.s_backend.domains.edu.controller;

import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.model.EUserPreferences;
import com.talentboozt.s_backend.domains.edu.service.EduPersonalizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/edu/personalization")
public class EduPersonalizationController {

    private final EduPersonalizationService personalizationService;

    public EduPersonalizationController(EduPersonalizationService personalizationService) {
        this.personalizationService = personalizationService;
    }

    @GetMapping("/preferences/{userId}")
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE')")
    public ResponseEntity<EUserPreferences> getPreferences(@PathVariable String userId) {
        return ResponseEntity.ok(personalizationService.getPreferences(userId));
    }

    @PutMapping("/preferences/{userId}")
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE')")
    public ResponseEntity<EUserPreferences> updatePreferences(
            @PathVariable String userId,
            @RequestBody EUserPreferences update) {
        return ResponseEntity.ok(personalizationService.updatePreferences(userId, update));
    }

    @GetMapping("/recommendations/{userId}")
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE')")
    public ResponseEntity<List<ECourses>> getRecommendations(@PathVariable String userId) {
        return ResponseEntity.ok(personalizationService.getRecommendations(userId));
    }
}
