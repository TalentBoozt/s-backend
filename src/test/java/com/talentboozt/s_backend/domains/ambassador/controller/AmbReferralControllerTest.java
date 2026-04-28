package com.talentboozt.s_backend.domains.ambassador.controller;

import com.talentboozt.s_backend.domains.ambassador.model.AmbReferralModel;
import com.talentboozt.s_backend.domains.ambassador.service.AmbReferralService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AmbReferralControllerTest {

    @Mock
    private AmbReferralService referralService;

    @InjectMocks
    private AmbReferralController referralController;

    @Test
    void addReferralReturnsCreatedReferral() {
        AmbReferralModel referralModel = new AmbReferralModel();
        AmbReferralModel createdReferral = new AmbReferralModel();
        createdReferral.setId("123");
        when(referralService.addReferral(referralModel)).thenReturn(createdReferral);

        AmbReferralModel result = referralController.addReferral(referralModel);

        assertEquals(createdReferral, result);
    }

    @Test
    void getReferralReturnsListForValidReferralCode() {
        String referralCode = "ABC123";
        List<AmbReferralModel> referrals = List.of(new AmbReferralModel(), new AmbReferralModel());
        when(referralService.getReferral(referralCode)).thenReturn(referrals);

        List<AmbReferralModel> result = referralController.getReferral(referralCode);

        assertEquals(referrals, result);
    }

    @Test
    void getReferralByAmbassadorReturnsListForValidAmbassadorId() {
        String ambassadorId = "amb123";
        List<AmbReferralModel> referrals = List.of(new AmbReferralModel(), new AmbReferralModel());
        when(referralService.getReferralByAmbassador(ambassadorId)).thenReturn(referrals);

        List<AmbReferralModel> result = referralController.getReferralByAmbassador(ambassadorId);

        assertEquals(referrals, result);
    }

    @Test
    void updateReferralReturnsUpdatedReferral() {
        String id = "123";
        AmbReferralModel referralModel = new AmbReferralModel();
        AmbReferralModel updatedReferral = new AmbReferralModel();
        updatedReferral.setId(id);
        when(referralService.updateReferral(id, referralModel)).thenReturn(updatedReferral);

        AmbReferralModel result = referralController.updateReferral(id, referralModel);

        assertEquals(updatedReferral, result);
    }

    @Test
    void getReferralThrowsExceptionForInvalidReferralCode() {
        String referralCode = "invalid";
        when(referralService.getReferral(referralCode)).thenThrow(new IllegalArgumentException("Referral not found"));

        assertThrows(IllegalArgumentException.class, () -> referralController.getReferral(referralCode));
    }
}
