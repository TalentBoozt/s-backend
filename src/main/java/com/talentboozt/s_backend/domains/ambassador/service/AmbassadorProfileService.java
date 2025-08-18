package com.talentboozt.s_backend.domains.ambassador.service;

import com.talentboozt.s_backend.domains.ambassador.model.AmbassadorProfileModel;
import com.talentboozt.s_backend.domains.ambassador.repository.AmbassadorProfileRepository;
import com.talentboozt.s_backend.domains.auth.model.CredentialsModel;
import com.talentboozt.s_backend.domains.auth.repository.CredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class AmbassadorProfileService {

    private final AmbassadorProfileRepository ambassadorProfileRepository;
    private final AmbassadorLevelService ambassadorLevelService;
    private final CredentialsRepository credentialsRepository;

    public AmbassadorProfileService(AmbassadorProfileRepository ambassadorProfileRepository, AmbassadorLevelService ambassadorLevelService, CredentialsRepository credentialsRepository) {
        this.ambassadorProfileRepository = ambassadorProfileRepository;
        this.ambassadorLevelService = ambassadorLevelService;
        this.credentialsRepository = credentialsRepository;
    }

    public AmbassadorProfileModel applyAmbassador(AmbassadorProfileModel request) {
        AmbassadorProfileModel existingProfile = ambassadorProfileRepository.findByEmail(request.getEmail());
        if (existingProfile != null) {
            // If a profile with the same email already exists, return it
            return existingProfile;
        }
        AmbassadorProfileModel profile = new AmbassadorProfileModel();
        profile.setName(request.getName());
        profile.setEmail(request.getEmail());
        profile.setMotivation(request.getMotivation());
        profile.setProfileLink(request.getProfileLink());
        profile.setConsentGiven(request.isConsentGiven());
        profile.setEmployeeId(request.getEmployeeId());
        profile.setReferralCode(generateReferralCode(request.getName()));
        profile.setAppliedAt(Instant.now());
        profile.setLevel("BRONZE");
        profile.setApplicationStatus("REQUESTED");
        profile.setStatus("REQUESTED");
        profile.setActive(false);

        return ambassadorProfileRepository.save(profile);
    }

    public AmbassadorProfileModel addAmbassadorProfile(AmbassadorProfileModel ambassadorProfileModel) {
        return ambassadorProfileRepository.save(ambassadorProfileModel);
    }

    public AmbassadorProfileModel getAmbassadorProfile(String id) {
        Optional<AmbassadorProfileModel> ambassadorProfileModel = ambassadorProfileRepository.findById(id);
        return ambassadorProfileModel.orElse(null);
    }

    public AmbassadorProfileModel updateAmbassadorProfile(String id, AmbassadorProfileModel ambassadorProfileModel) {
        AmbassadorProfileModel ambassadorProfile = ambassadorProfileRepository.findById(id).orElse(null);
        if (ambassadorProfile != null) {
            ambassadorProfile.setEmployeeId(ambassadorProfileModel.getEmployeeId());
            ambassadorProfile.setLevel(ambassadorLevelService.evaluateLevel(ambassadorProfile));
            ambassadorProfile.setTotalReferrals(ambassadorProfileModel.getTotalReferrals());
            ambassadorProfile.setCoursePurchasesByReferrals(ambassadorProfileModel.getCoursePurchasesByReferrals());
            ambassadorProfile.setHostedSessions(ambassadorProfileModel.getHostedSessions());
            ambassadorProfile.setTrainingSessionsAttended(ambassadorProfileModel.getTrainingSessionsAttended());
            ambassadorProfile.setActive(ambassadorProfileModel.isActive());
            ambassadorProfile.setJoinedAt(ambassadorProfileModel.getJoinedAt());
            ambassadorProfile.setLastActivity(ambassadorProfileModel.getLastActivity());
            ambassadorProfile.setBadges(ambassadorProfileModel.getBadges());
            ambassadorProfile.setReferralCode(ambassadorProfileModel.getReferralCode());
            ambassadorProfile.setStatus(ambassadorProfileModel.getStatus());
            ambassadorProfile.setInterviewNote(ambassadorProfileModel.getInterviewNote());
            ambassadorProfile.setBadgeHistory(ambassadorProfileModel.getBadgeHistory());
            ambassadorProfile.setPerks(ambassadorProfileModel.getPerks());
            return ambassadorProfileRepository.save(ambassadorProfile);
        }
        return null;
    }

    public Iterable<AmbassadorProfileModel> getAllAmbassadorProfiles() {
        return ambassadorProfileRepository.findAll();
    }

    public AmbassadorProfileModel approveAmbassadorProfile(String id) {
        Optional<AmbassadorProfileModel> ambassadorProfileModel = ambassadorProfileRepository.findById(id);
        if (ambassadorProfileModel.isPresent()) {
            AmbassadorProfileModel ambassadorProfile = ambassadorProfileModel.get();
            ambassadorProfile.setStatus("ACTIVE");
            ambassadorProfile.setApplicationStatus("ACCEPTED");
            ambassadorProfile.setActive(true);
            ambassadorProfile.setJoinedAt(Instant.now());
            ambassadorProfile.setLastActivity(Instant.now());
            AmbassadorProfileModel savedAmbassadorProfile = ambassadorProfileRepository.save(ambassadorProfile);

            CredentialsModel credentialsModel = credentialsRepository.findByEmployeeId(savedAmbassadorProfile.getEmployeeId()).orElse(null);
            if (credentialsModel != null) {
                credentialsModel.setAmbassador(true);
                credentialsModel.setAmbassadorId(savedAmbassadorProfile.getId());
                credentialsModel.setActive(true);
                credentialsRepository.save(credentialsModel);
            }
            return ambassadorProfile;
        }
        return null;
    }

    public AmbassadorProfileModel rejectAmbassadorProfile(String id) {
        Optional<AmbassadorProfileModel> ambassadorProfileModel = ambassadorProfileRepository.findById(id);
        if (ambassadorProfileModel.isPresent()) {
            AmbassadorProfileModel ambassadorProfile = ambassadorProfileModel.get();
            ambassadorProfile.setStatus("REJECTED");
            ambassadorProfile.setApplicationStatus("REJECTED");
            ambassadorProfile.setLastActivity(Instant.now());
            return ambassadorProfileRepository.save(ambassadorProfile);
        }
        return null;
    }

    public AmbassadorProfileModel suspendAmbassadorProfile(String id) {
        Optional<AmbassadorProfileModel> ambassadorProfileModel = ambassadorProfileRepository.findById(id);
        if (ambassadorProfileModel.isPresent()) {
            AmbassadorProfileModel ambassadorProfile = ambassadorProfileModel.get();
            ambassadorProfile.setStatus("SUSPENDED");
            ambassadorProfile.setLastActivity(Instant.now());
            return ambassadorProfileRepository.save(ambassadorProfile);
        }
        return null;
    }

    public AmbassadorProfileModel applicationAcceptance(AmbassadorProfileModel ambassadorProfileModel) {
        Optional<AmbassadorProfileModel> ambassadorProfile = ambassadorProfileRepository.findById(ambassadorProfileModel.getId());
        if (ambassadorProfile.isPresent()) {
            AmbassadorProfileModel ambassadorProfile1 = ambassadorProfile.get();
            ambassadorProfile1.setApplicationStatus(ambassadorProfileModel.getApplicationStatus());
            ambassadorProfile1.setInterviewNote(ambassadorProfileModel.getInterviewNote());
            ambassadorProfile1.setLastActivity(Instant.now());
            return ambassadorProfileRepository.save(ambassadorProfile1);
        }
        return null;
    }

    public AmbassadorProfileModel promoteAmbassador(String id) {
        Optional<AmbassadorProfileModel> ambassadorProfile = ambassadorProfileRepository.findById(id);
        if (ambassadorProfile.isPresent()) {
            AmbassadorProfileModel profile = ambassadorProfile.get();

            // Check if the level is not already at the highest level (PLATINUM)
            if (!profile.getLevel().equals("PLATINUM")) {
                switch (profile.getLevel()) {
                    case "BRONZE" -> profile.setLevel("SILVER");
                    case "SILVER" -> profile.setLevel("GOLD");
                    case "GOLD" -> profile.setLevel("DIAMOND");
                    case "DIAMOND" -> profile.setLevel("PLATINUM");
                }
                profile.setLastActivity(Instant.now());
                profile = ambassadorProfileRepository.save(profile);
            }
            return profile;
        }
        return null;
    }

    public AmbassadorProfileModel demoteAmbassador(String id) {
        Optional<AmbassadorProfileModel> ambassadorProfile = ambassadorProfileRepository.findById(id);
        if (ambassadorProfile.isPresent()) {
            AmbassadorProfileModel profile = ambassadorProfile.get();
            // Check if the level is not already at the lowest level (BRONZE)
            if (!profile.getLevel().equals("BRONZE")) {
                switch (profile.getLevel()) {
                    case "SILVER" -> profile.setLevel("BRONZE");
                    case "GOLD" -> profile.setLevel("SILVER");
                    case "DIAMOND" -> profile.setLevel("GOLD");
                    case "PLATINUM" -> profile.setLevel("DIAMOND");
                }
                profile.setLastActivity(Instant.now());
                profile = ambassadorProfileRepository.save(profile);
            }
            return profile;
        }
        return null;
    }

    private String generateReferralCode(String name) {
        String prefix = name.replaceAll("[^A-Za-z]", "").substring(0, Math.min(3, name.length())).toUpperCase();
        String randomPart = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6).toUpperCase();
        return prefix + randomPart;
    }
}
