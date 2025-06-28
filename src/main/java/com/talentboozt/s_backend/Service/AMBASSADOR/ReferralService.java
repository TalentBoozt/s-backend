package com.talentboozt.s_backend.Service.AMBASSADOR;

import com.talentboozt.s_backend.Model.AMBASSADOR.ReferralModel;
import com.talentboozt.s_backend.Repository.AMBASSADOR.ReferralRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReferralService {

    @Autowired
    ReferralRepository referralRepository;

    public ReferralModel addReferral(ReferralModel referralModel) {
        return referralRepository.save(referralModel);
    }

    public List<ReferralModel> getReferral(String referralCode) {
        return referralRepository.findAllByReferralCode(referralCode);
    }

    public List<ReferralModel> getReferralByAmbassador(String ambassadorId) {
        return referralRepository.findAllByAmbassadorId(ambassadorId);
    }

    public ReferralModel updateReferral(String id, ReferralModel referralModel) {
        Optional<ReferralModel> existingReferral = referralRepository.findById(id);
        if (existingReferral.isPresent()) {
            ReferralModel referral = existingReferral.get();
            referral.setReferralCode(referralModel.getReferralCode());
            referral.setAmbassadorId(referralModel.getAmbassadorId());
            referral.setReferredUserId(referralModel.getReferredUserId());
            referral.setReferredAt(referralModel.getReferredAt());
            referral.setReferredPlatform(referralModel.getReferredPlatform());
            referral.setCourseEnrolled(referralModel.isCourseEnrolled());
            referral.setEnrolledAt(referralModel.getEnrolledAt());
            return referralRepository.save(referral);
        }
        return null;
    }
}
