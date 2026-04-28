package com.talentboozt.s_backend.domains.ambassador.service;

import com.talentboozt.s_backend.domains.ambassador.model.AmbReferralModel;
import com.talentboozt.s_backend.domains.ambassador.repository.mongodb.AmbReferralRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AmbReferralService {

    @Autowired
    AmbReferralRepository referralRepository;

    public AmbReferralModel addReferral(AmbReferralModel referralModel) {
        return referralRepository.save(Objects.requireNonNull(referralModel));
    }

    public List<AmbReferralModel> getReferral(String referralCode) {
        return referralRepository.findAllByReferralCode(referralCode);
    }

    public List<AmbReferralModel> getReferralByAmbassador(String ambassadorId) {
        return referralRepository.findAllByAmbassadorId(ambassadorId);
    }

    public AmbReferralModel updateReferral(String id, AmbReferralModel referralModel) {
        Optional<AmbReferralModel> existingReferral = referralRepository.findById(Objects.requireNonNull(id));
        if (existingReferral.isPresent()) {
            AmbReferralModel referral = existingReferral.get();
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
