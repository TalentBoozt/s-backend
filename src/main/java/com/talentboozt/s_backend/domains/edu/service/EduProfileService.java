package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.model.EProfiles;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EProfilesRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class EduProfileService {

    private static final long MAX_AVATAR_BYTES = 5 * 1024 * 1024;

    private final EProfilesRepository profileRepository;
    private final EUserRepository userRepository;

    public EduProfileService(EProfilesRepository profileRepository, EUserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    public EProfiles getProfileByUserId(String userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found for userId: " + userId));
    }

    public EProfiles updateProfile(String userId, EProfiles updatedProfile) {
        EProfiles existing = getProfileByUserId(userId);

        existing.setFirstName(updatedProfile.getFirstName());
        existing.setLastName(updatedProfile.getLastName());
        existing.setPublicEmail(updatedProfile.getPublicEmail());
        existing.setPublicPhone(updatedProfile.getPublicPhone());
        existing.setAvatarUrl(updatedProfile.getAvatarUrl());
        existing.setBio(updatedProfile.getBio());
        existing.setSocialLinks(updatedProfile.getSocialLinks());
        existing.setIndustry(updatedProfile.getIndustry());
        existing.setCompany(updatedProfile.getCompany());
        existing.setJobTitle(updatedProfile.getJobTitle());
        existing.setExperience(updatedProfile.getExperience());
        existing.setEducation(updatedProfile.getEducation());
        existing.setSkills(updatedProfile.getSkills());
        existing.setLanguages(updatedProfile.getLanguages());
        existing.setInterests(updatedProfile.getInterests());
        existing.setPrivacySettings(updatedProfile.getPrivacySettings());
        existing.setNotificationSettings(updatedProfile.getNotificationSettings());
        existing.setUpdatedAt(Instant.now());

        return profileRepository.save(existing);
    }

    /**
     * Saves an image under {@code uploads/edu/profiles/{userId}/} and updates {@link EProfiles#avatarUrl}
     * to a public path served by {@link com.talentboozt.s_backend.config.StaticResourceConfig}.
     */
    public Map<String, String> uploadAvatar(String userId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }
        if (file.getSize() > MAX_AVATAR_BYTES) {
            throw new IllegalArgumentException("Avatar must be at most 5MB");
        }
        String ct = file.getContentType();
        if (ct == null || !ct.startsWith("image/")) {
            throw new IllegalArgumentException("Only image uploads are allowed");
        }
        String ext = extensionForContentType(ct);
        Path base = Paths.get("uploads", "edu", "profiles", userId);
        String filename = UUID.randomUUID() + ext;
        String url = "/uploads/edu/profiles/" + userId + "/" + filename;
        try {
            Files.createDirectories(base);
            Path dest = base.resolve(filename);
            Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save avatar", e);
        }

        EProfiles existing = getProfileByUserId(userId);
        existing.setAvatarUrl(url);
        existing.setUpdatedAt(Instant.now());
        profileRepository.save(existing);
        return Map.of("avatarUrl", url);
    }

    public void deleteProfileAndUser(String userId) {
        profileRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);
    }

    private static String extensionForContentType(String contentType) {
        return switch (contentType.toLowerCase()) {
            case "image/jpeg", "image/jpg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            default -> throw new IllegalArgumentException("Unsupported image type: " + contentType);
        };
    }
}
