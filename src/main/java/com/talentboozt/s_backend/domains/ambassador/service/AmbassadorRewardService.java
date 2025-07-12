package com.talentboozt.s_backend.domains.ambassador.service;

import com.talentboozt.s_backend.domains.ambassador.model.AmbassadorProfileModel;
import com.talentboozt.s_backend.domains.ambassador.model.AmbassadorRewardModel;
import com.talentboozt.s_backend.domains.ambassador.repository.AmbassadorProfileRepository;
import com.talentboozt.s_backend.domains.ambassador.repository.AmbassadorRewardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AmbassadorRewardService {

    @Autowired
    AmbassadorRewardRepository ambassadorRewardRepository;

    @Autowired
    AmbassadorProfileRepository ambassadorProfileRepository;

    public AmbassadorRewardModel addAmbassadorReward(String ambassadorId, AmbassadorRewardModel ambassadorRewardModel) {
        Optional<AmbassadorProfileModel> ambassadorProfileModel = ambassadorProfileRepository.findById(ambassadorId);
        if (ambassadorProfileModel.isPresent()) {
            ambassadorRewardModel.setAmbassadorId(ambassadorId);
            return ambassadorRewardRepository.save(ambassadorRewardModel);
        }
        return null;
    }

    public Iterable<AmbassadorRewardModel> getAmbassadorRewards(String ambassadorId) {
        return ambassadorRewardRepository.findAllByAmbassadorId(ambassadorId);
    }

    public AmbassadorRewardModel getAmbassadorReward(String id) {
        Optional<AmbassadorRewardModel> ambassadorRewardModel = ambassadorRewardRepository.findById(id);
        return ambassadorRewardModel.orElse(null);
    }

    public AmbassadorRewardModel updateAmbassadorRewardStatus(String id, String status) {
        Optional<AmbassadorRewardModel> ambassadorRewardModel = ambassadorRewardRepository.findById(id);
        if (ambassadorRewardModel.isPresent()) {
            ambassadorRewardModel.get().setStatus(status);
            return ambassadorRewardRepository.save(ambassadorRewardModel.get());
        }
        return null;
    }

    public AmbassadorRewardModel updateAmbassadorReward(String id, AmbassadorRewardModel ambassadorRewardModel) {
        Optional<AmbassadorRewardModel> ambassadorRewardModel1 = ambassadorRewardRepository.findById(id);
        if (ambassadorRewardModel1.isPresent()) {
            ambassadorRewardModel1.get().setRewardType(ambassadorRewardModel.getRewardType());
            ambassadorRewardModel1.get().setStatus(ambassadorRewardModel.getStatus());
            ambassadorRewardModel1.get().setIssuedAt(ambassadorRewardModel.getIssuedAt());
            ambassadorRewardModel1.get().setRedeemedAt(ambassadorRewardModel.getRedeemedAt());
            return ambassadorRewardRepository.save(ambassadorRewardModel1.get());
        }
        return null;
    }

    public void deleteAmbassadorReward(String id) {
        ambassadorRewardRepository.deleteById(id);
    }

    public Iterable<AmbassadorRewardModel> getAllAmbassadorRewards() {
        return ambassadorRewardRepository.findAll();
    }

    public AmbassadorRewardModel markAsShipped(String id) {
        Optional<AmbassadorRewardModel> ambassadorRewardModel = ambassadorRewardRepository.findById(id);
        if (ambassadorRewardModel.isPresent()) {
            ambassadorRewardModel.get().setStatus("SHIPPED");
            return ambassadorRewardRepository.save(ambassadorRewardModel.get());
        }
        return null;
    }
}
