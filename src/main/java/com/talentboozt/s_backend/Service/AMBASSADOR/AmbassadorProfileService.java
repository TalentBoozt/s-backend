package com.talentboozt.s_backend.Service.AMBASSADOR;

import com.talentboozt.s_backend.Model.AMBASSADOR.AmbassadorProfileModel;
import com.talentboozt.s_backend.Repository.AMBASSADOR.AmbassadorProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AmbassadorProfileService {

    @Autowired
    private AmbassadorProfileRepository ambassadorProfileRepository;
    @Autowired
    private AmbassadorLevelService ambassadorLevelService;

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
}
