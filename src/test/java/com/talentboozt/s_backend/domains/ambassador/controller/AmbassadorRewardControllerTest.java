package com.talentboozt.s_backend.domains.ambassador.controller;

import com.talentboozt.s_backend.domains.ambassador.model.AmbassadorRewardModel;
import com.talentboozt.s_backend.domains.ambassador.service.AmbassadorRewardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AmbassadorRewardControllerTest {

    @Mock
    private AmbassadorRewardService ambassadorRewardService;

    @InjectMocks
    private AmbassadorRewardController ambassadorRewardController;

    @Test
    void addAmbassadorRewardReturnsCreatedReward() {
        String ambassadorId = "amb123";
        AmbassadorRewardModel rewardModel = new AmbassadorRewardModel();
        AmbassadorRewardModel createdReward = new AmbassadorRewardModel();
        createdReward.setId("reward123");
        when(ambassadorRewardService.addAmbassadorReward(ambassadorId, rewardModel)).thenReturn(createdReward);

        AmbassadorRewardModel result = ambassadorRewardController.addAmbassadorReward(ambassadorId, rewardModel);

        assertEquals(createdReward, result);
    }

    @Test
    void getAmbassadorRewardsReturnsListForValidAmbassadorId() {
        String ambassadorId = "amb123";
        List<AmbassadorRewardModel> rewards = List.of(new AmbassadorRewardModel(), new AmbassadorRewardModel());
        when(ambassadorRewardService.getAmbassadorRewards(ambassadorId)).thenReturn(rewards);

        Iterable<AmbassadorRewardModel> result = ambassadorRewardController.getAmbassadorRewards(ambassadorId);

        assertEquals(rewards, result);
    }

    @Test
    void getAmbassadorRewardReturnsRewardForValidId() {
        String rewardId = "reward123";
        AmbassadorRewardModel reward = new AmbassadorRewardModel();
        reward.setId(rewardId);
        when(ambassadorRewardService.getAmbassadorReward(rewardId)).thenReturn(reward);

        AmbassadorRewardModel result = ambassadorRewardController.getAmbassadorReward(rewardId);

        assertEquals(reward, result);
    }

    @Test
    void updateAmbassadorRewardStatusUpdatesSuccessfully() {
        String rewardId = "reward123";
        String status = "SHIPPED";
        AmbassadorRewardModel updatedReward = new AmbassadorRewardModel();
        updatedReward.setId(rewardId);
        updatedReward.setStatus(status);
        when(ambassadorRewardService.updateAmbassadorRewardStatus(rewardId, status)).thenReturn(updatedReward);

        AmbassadorRewardModel result = ambassadorRewardController.updateAmbassadorRewardStatus(rewardId, status);

        assertEquals(updatedReward, result);
    }

    @Test
    void deleteAmbassadorRewardDeletesSuccessfully() {
        String rewardId = "reward123";

        ResponseEntity<?> response = ambassadorRewardController.deleteAmbassadorReward(rewardId);

        verify(ambassadorRewardService).deleteAmbassadorReward(rewardId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
