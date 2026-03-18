package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.model.EProfiles;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EProfilesRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class EduProfileService {

    private final EProfilesRepository profileRepository;

    public EduProfileService(EProfilesRepository profileRepository) {
        this.profileRepository = profileRepository;
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
}
