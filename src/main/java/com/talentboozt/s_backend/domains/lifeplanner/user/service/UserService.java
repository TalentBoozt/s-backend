package com.talentboozt.s_backend.domains.lifeplanner.user.service;

import org.springframework.stereotype.Service;
import com.talentboozt.s_backend.domains.lifeplanner.user.model.User;
import com.talentboozt.s_backend.domains.lifeplanner.user.model.UserProfile;
import com.talentboozt.s_backend.domains.lifeplanner.user.model.UserPreferences;
import com.talentboozt.s_backend.domains.lifeplanner.user.repository.mongodb.UserRepository;
import com.talentboozt.s_backend.domains.lifeplanner.user.repository.mongodb.UserProfileRepository;
import com.talentboozt.s_backend.domains.lifeplanner.user.repository.mongodb.UserPreferencesRepository;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserPreferencesRepository userPreferencesRepository;

    public User createUser(User user) {
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        return userRepository.save(user);
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public UserProfile saveProfile(UserProfile profile) {
        profile.setUpdatedAt(Instant.now());
        if (profile.getCreatedAt() == null) {
            profile.setCreatedAt(Instant.now());
        }
        return userProfileRepository.save(profile);
    }

    public Optional<UserProfile> getProfileByUserId(String userId) {
        return userProfileRepository.findByUserId(userId);
    }

    public UserPreferences savePreferences(UserPreferences prefs) {
        return userPreferencesRepository.save(prefs);
    }

    public Optional<UserPreferences> getPreferencesByUserId(String userId) {
        return userPreferencesRepository.findByUserId(userId);
    }
}
