package com.talentboozt.s_backend.domains.ambassador.service;

import com.talentboozt.s_backend.domains.ambassador.model.ReferralModel;
import com.talentboozt.s_backend.domains.ambassador.repository.mongodb.ReferralRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ReferralService {

    @Autowired
    ReferralRepository referralRepository;

    public ReferralModel addReferral(ReferralModel referralModel) {
        return referralRepository.save(Objects.requireNonNull(referralModel));
    }

    public List<ReferralModel> getReferral(String referralCode) {
        return referralRepository.findAllByReferralCode(referralCode);
    }

    public List<ReferralModel> getReferralByAmbassador(String ambassadorId) {
        return referralRepository.findAllByAmbassadorId(ambassadorId);
    }

    public ReferralModel updateReferral(String id, ReferralModel referralModel) {
        Optional<ReferralModel> existingReferral = referralRepository.findById(Objects.requireNonNull(id));
        if (existingReferral.isPresent()) {
            ReferralModel referral = existingReferral.get();
            referral.setReferralCode(referralModel.getReferralCode());
            referral.setAmbassadorId(referralModel.getAmbassadorId());
            referral.setReferredUserId(referralModel.getReferredUserId());
            referral.setReferredAt(referralModel.getReferredAt());
            referral.setReferredPlatform(referralModel.getReferredPlatform());
            referral.setCourseId(referralModel.getCourseId());
            referral.setCourseEnrolled(referralModel.isCourseEnrolled());
            referral.setEnrolledAt(referralModel.getEnrolledAt());
            return referralRepository.save(referral);
        }
        return null;
    }
}
