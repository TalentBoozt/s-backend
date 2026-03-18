package com.talentboozt.s_backend.domains.edu.controller;

import com.talentboozt.s_backend.domains.edu.model.EProfiles;
import com.talentboozt.s_backend.domains.edu.service.EduProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/edu/profiles")
public class EduProfileController {

    private final EduProfileService profileService;

    public EduProfileController(EduProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<EProfiles> getProfile(@PathVariable String userId) {
        return ResponseEntity.ok(profileService.getProfileByUserId(userId));
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('INSTRUCTOR')")
    public ResponseEntity<EProfiles> updateProfile(@PathVariable String userId, @RequestBody EProfiles profile) {
        return ResponseEntity.ok(profileService.updateProfile(userId, profile));
    }
}
