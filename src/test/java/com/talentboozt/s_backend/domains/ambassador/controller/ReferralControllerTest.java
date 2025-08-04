package com.talentboozt.s_backend.domains.ambassador.controller;

import com.talentboozt.s_backend.domains.ambassador.model.ReferralModel;
import com.talentboozt.s_backend.domains.ambassador.service.ReferralService;
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
class ReferralControllerTest {

    @Mock
    private ReferralService referralService;

    @InjectMocks
    private ReferralController referralController;

    @Test
    void addReferralReturnsCreatedReferral() {
        ReferralModel referralModel = new ReferralModel();
        ReferralModel createdReferral = new ReferralModel();
        createdReferral.setId("123");
        when(referralService.addReferral(referralModel)).thenReturn(createdReferral);

        ReferralModel result = referralController.addReferral(referralModel);

        assertEquals(createdReferral, result);
    }

    @Test
    void getReferralReturnsListForValidReferralCode() {
        String referralCode = "ABC123";
        List<ReferralModel> referrals = List.of(new ReferralModel(), new ReferralModel());
        when(referralService.getReferral(referralCode)).thenReturn(referrals);

        List<ReferralModel> result = referralController.getReferral(referralCode);

        assertEquals(referrals, result);
    }

    @Test
    void getReferralByAmbassadorReturnsListForValidAmbassadorId() {
        String ambassadorId = "amb123";
        List<ReferralModel> referrals = List.of(new ReferralModel(), new ReferralModel());
        when(referralService.getReferralByAmbassador(ambassadorId)).thenReturn(referrals);

        List<ReferralModel> result = referralController.getReferralByAmbassador(ambassadorId);

        assertEquals(referrals, result);
    }

    @Test
    void updateReferralReturnsUpdatedReferral() {
        String id = "123";
        ReferralModel referralModel = new ReferralModel();
        ReferralModel updatedReferral = new ReferralModel();
        updatedReferral.setId(id);
        when(referralService.updateReferral(id, referralModel)).thenReturn(updatedReferral);

        ReferralModel result = referralController.updateReferral(id, referralModel);

        assertEquals(updatedReferral, result);
    }

    @Test
    void getReferralThrowsExceptionForInvalidReferralCode() {
        String referralCode = "invalid";
        when(referralService.getReferral(referralCode)).thenThrow(new IllegalArgumentException("Referral not found"));

        assertThrows(IllegalArgumentException.class, () -> referralController.getReferral(referralCode));
    }
}
