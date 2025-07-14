package com.talentboozt.s_backend.domains.ambassador.service;

import com.talentboozt.s_backend.domains.ambassador.model.AmbassadorProfileModel;
import com.talentboozt.s_backend.domains.ambassador.repository.AmbassadorProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class AmbassadorProfileService {

    @Autowired
    private AmbassadorProfileRepository ambassadorProfileRepository;
    @Autowired
    private AmbassadorLevelService ambassadorLevelService;

    public AmbassadorProfileModel applyAmbassador(AmbassadorProfileModel request) {
        AmbassadorProfileModel profile = new AmbassadorProfileModel();
        profile.setName(request.getName());
        profile.setEmail(request.getEmail());
        profile.setMotivation(request.getMotivation());
        profile.setProfileLink(request.getProfileLink());
        profile.setConsentGiven(request.isConsentGiven());
        profile.setEmployeeId(request.getEmployeeId());
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
            return ambassadorProfileRepository.save(ambassadorProfile);
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
            AmbassadorProfileModel ambassadorProfile1 = ambassadorProfile.get();
            switch (ambassadorProfile1.getLevel()) {
                case "BRONZE" -> ambassadorProfile1.setLevel("SILVER");
                case "SILVER" -> ambassadorProfile1.setLevel("GOLD");
                case "GOLD" -> ambassadorProfile1.setLevel("DIAMOND");
                case "DIAMOND" -> ambassadorProfile1.setLevel("PLATINUM");
            }
            ambassadorProfile1.setLastActivity(Instant.now());
            return ambassadorProfileRepository.save(ambassadorProfile1);
        }
        return null;
    }

    public AmbassadorProfileModel demoteAmbassador(String id) {
        Optional<AmbassadorProfileModel> ambassadorProfile = ambassadorProfileRepository.findById(id);
        if (ambassadorProfile.isPresent()) {
            AmbassadorProfileModel ambassadorProfile1 = ambassadorProfile.get();
            switch (ambassadorProfile1.getLevel()) {
                case "SILVER" -> ambassadorProfile1.setLevel("BRONZE");
                case "GOLD" -> ambassadorProfile1.setLevel("SILVER");
                case "DIAMOND" -> ambassadorProfile1.setLevel("GOLD");
                case "PLATINUM" -> ambassadorProfile1.setLevel("DIAMOND");
            }
            ambassadorProfile1.setLastActivity(Instant.now());
            return ambassadorProfileRepository.save(ambassadorProfile1);
        }
        return null;
    }
}
