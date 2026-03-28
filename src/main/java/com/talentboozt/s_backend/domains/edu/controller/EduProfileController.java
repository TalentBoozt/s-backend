package com.talentboozt.s_backend.domains.edu.controller;

import com.talentboozt.s_backend.domains.edu.model.EProfiles;
import com.talentboozt.s_backend.domains.edu.service.EduProfileService;
import com.talentboozt.s_backend.shared.security.model.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

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
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<EProfiles> updateProfile(
            @PathVariable String userId,
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestBody EProfiles profile) {
        if (principal == null || !userId.equals(principal.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(profileService.updateProfile(userId, profile));
    }

    @PostMapping(value = "/{userId}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<Map<String, String>> uploadAvatar(
            @PathVariable String userId,
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestPart("file") MultipartFile file) {
        if (principal == null || !userId.equals(principal.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(profileService.uploadAvatar(userId, file));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<Void> deleteAccount(
            @PathVariable String userId,
            @AuthenticationPrincipal CustomUserDetails principal) {
        if (principal == null || !userId.equals(principal.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        profileService.deleteProfileAndUser(userId);
        return ResponseEntity.noContent().build();
    }
}
