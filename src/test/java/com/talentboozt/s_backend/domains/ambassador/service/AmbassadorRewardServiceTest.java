package com.talentboozt.s_backend.domains.ambassador.service;

import com.talentboozt.s_backend.domains.ambassador.model.AmbassadorProfileModel;
import com.talentboozt.s_backend.domains.ambassador.model.AmbassadorRewardModel;
import com.talentboozt.s_backend.domains.ambassador.repository.mongodb.AmbassadorProfileRepository;
import com.talentboozt.s_backend.domains.ambassador.repository.mongodb.AmbassadorRewardRepository;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AmbassadorRewardServiceTest {

    @Mock
    private AmbassadorRewardRepository ambassadorRewardRepository;

    @Mock
    private AmbassadorProfileRepository ambassadorProfileRepository;

    @InjectMocks
    private AmbassadorRewardService ambassadorRewardService;

    @Test
    void addAmbassadorReward_savesRewardWhenAmbassadorExists() {
        String ambassadorId = "123";
        AmbassadorRewardModel reward = new AmbassadorRewardModel();
        reward.setTaskId("task1");
        reward.setRewardType("SWAG");

        when(ambassadorProfileRepository.findById(ambassadorId)).thenReturn(Optional.of(new AmbassadorProfileModel()));
        when(ambassadorRewardRepository.save(any(AmbassadorRewardModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AmbassadorRewardModel result = ambassadorRewardService.addAmbassadorReward(ambassadorId, reward);

        assertNotNull(result);
        assertEquals(ambassadorId, result.getAmbassadorId());
        verify(ambassadorRewardRepository).save(reward);
    }

    @Test
    void addAmbassadorReward_returnsNullWhenAmbassadorDoesNotExist() {
        String ambassadorId = "123";
        AmbassadorRewardModel reward = new AmbassadorRewardModel();

        when(ambassadorProfileRepository.findById(ambassadorId)).thenReturn(Optional.empty());

        AmbassadorRewardModel result = ambassadorRewardService.addAmbassadorReward(ambassadorId, reward);

        assertNull(result);
        verify(ambassadorRewardRepository, never()).save(any(AmbassadorRewardModel.class));
    }

    @Test
    void updateAmbassadorRewardStatus_updatesStatusWhenRewardExists() {
        String rewardId = "reward1";
        String newStatus = "REDEEMED";
        AmbassadorRewardModel reward = new AmbassadorRewardModel();
        reward.setId(rewardId);
        reward.setStatus("PENDING");

        when(ambassadorRewardRepository.findById(rewardId)).thenReturn(Optional.of(reward));
        when(ambassadorRewardRepository.save(any(AmbassadorRewardModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AmbassadorRewardModel result = ambassadorRewardService.updateAmbassadorRewardStatus(rewardId, newStatus);

        assertNotNull(result);
        assertEquals(newStatus, result.getStatus());
        verify(ambassadorRewardRepository).save(reward);
    }

    @Test
    void updateAmbassadorRewardStatus_returnsNullWhenRewardDoesNotExist() {
        String rewardId = "reward1";
        String newStatus = "REDEEMED";

        when(ambassadorRewardRepository.findById(rewardId)).thenReturn(Optional.empty());

        AmbassadorRewardModel result = ambassadorRewardService.updateAmbassadorRewardStatus(rewardId, newStatus);

        assertNull(result);
        verify(ambassadorRewardRepository, never()).save(any(AmbassadorRewardModel.class));
    }

    @Test
    void markAsShipped_updatesStatusToShippedWhenRewardExists() {
        String rewardId = "reward1";
        AmbassadorRewardModel reward = new AmbassadorRewardModel();
        reward.setId(rewardId);
        reward.setStatus("PENDING");

        when(ambassadorRewardRepository.findById(rewardId)).thenReturn(Optional.of(reward));
        when(ambassadorRewardRepository.save(any(AmbassadorRewardModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AmbassadorRewardModel result = ambassadorRewardService.markAsShipped(rewardId);

        assertNotNull(result);
        assertEquals("SHIPPED", result.getStatus());
        verify(ambassadorRewardRepository).save(reward);
    }

    @Test
    void markAsShipped_returnsNullWhenRewardDoesNotExist() {
        String rewardId = "reward1";

        when(ambassadorRewardRepository.findById(rewardId)).thenReturn(Optional.empty());

        AmbassadorRewardModel result = ambassadorRewardService.markAsShipped(rewardId);

        assertNull(result);
        verify(ambassadorRewardRepository, never()).save(any(AmbassadorRewardModel.class));
    }
}
