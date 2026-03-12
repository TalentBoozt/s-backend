package com.talentboozt.s_backend.domains.lifeplanner.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.talentboozt.s_backend.domains.lifeplanner.user.model.User;
import com.talentboozt.s_backend.domains.lifeplanner.user.model.UserProfile;
import com.talentboozt.s_backend.domains.lifeplanner.user.model.UserPreferences;
import com.talentboozt.s_backend.domains.lifeplanner.user.service.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lifeplanner/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@RequestHeader("x-user-id") String userId) {
        return ResponseEntity.ok(userService.getOrCreateUser(userId));
    }

    // ── Profile ──

    @PostMapping("/profile")
    public ResponseEntity<UserProfile> saveProfile(@RequestBody UserProfile profile, @RequestHeader("x-user-id") String userId) {
        profile.setUserId(userId);
        UserProfile saved = userService.saveProfile(profile);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfile> getProfile(@RequestHeader("x-user-id") String userId) {
        return ResponseEntity.ok(userService.getOrCreateProfile(userId));
    }

    // ── Preferences ──

    @PostMapping("/preferences")
    public ResponseEntity<UserPreferences> savePreferences(@RequestBody UserPreferences prefs, @RequestHeader("x-user-id") String userId) {
        prefs.setUserId(userId);
        UserPreferences saved = userService.savePreferences(prefs);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/preferences")
    public ResponseEntity<UserPreferences> getPreferences(@RequestHeader("x-user-id") String userId) {
        return ResponseEntity.ok(userService.getOrCreatePreferences(userId));
    }
}
