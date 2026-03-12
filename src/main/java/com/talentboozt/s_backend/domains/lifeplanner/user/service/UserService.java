package com.talentboozt.s_backend.domains.lifeplanner.user.service;

import org.springframework.stereotype.Service;
import com.talentboozt.s_backend.domains.lifeplanner.user.model.User;
import com.talentboozt.s_backend.domains.lifeplanner.user.model.UserProfile;
import com.talentboozt.s_backend.domains.lifeplanner.user.model.UserPreferences;
import com.talentboozt.s_backend.domains.lifeplanner.user.repository.mongodb.UserRepository;
import com.talentboozt.s_backend.domains.lifeplanner.user.repository.mongodb.UserProfileRepository;
import com.talentboozt.s_backend.domains.lifeplanner.user.repository.mongodb.UserPreferencesRepository;
import com.talentboozt.s_backend.domains.user.repository.mongodb.EmployeeRepository;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserPreferencesRepository userPreferencesRepository;
    private final EmployeeRepository employeeRepository;

    public User getOrCreateUser(String userId) {
        return userRepository.findById(userId).orElseGet(() -> {
            User newUser = new User();
            newUser.setId(userId);
            
            // Try to sync with EmployeeModel if available
            employeeRepository.findById(userId).ifPresent(emp -> {
                newUser.setEmail(emp.getEmail());
                newUser.setName(emp.getFirstname() + " " + (emp.getLastname() != null ? emp.getLastname() : ""));
            });
            
            newUser.setCreatedAt(Instant.now());
            newUser.setUpdatedAt(Instant.now());
            return userRepository.save(newUser);
        });
    }

    public UserProfile getOrCreateProfile(String userId) {
        return userProfileRepository.findByUserId(userId).orElseGet(() -> {
            UserProfile newProfile = new UserProfile();
            newProfile.setUserId(userId);
            newProfile.setCreatedAt(Instant.now());
            newProfile.setUpdatedAt(Instant.now());
            return userProfileRepository.save(newProfile);
        });
    }

    public UserPreferences getOrCreatePreferences(String userId) {
        return userPreferencesRepository.findByUserId(userId).orElseGet(() -> {
            UserPreferences newPrefs = new UserPreferences();
            newPrefs.setUserId(userId);
            return userPreferencesRepository.save(newPrefs);
        });
    }

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
