package com.talentboozt.s_backend.domains.ambassador.service;

import com.talentboozt.s_backend.domains.ambassador.model.AmbReferralModel;
import com.talentboozt.s_backend.domains.ambassador.repository.mongodb.AmbReferralRepository;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReferralServiceTest {

    @Mock
    private AmbReferralRepository referralRepository;

    @InjectMocks
    private AmbReferralService referralService;

    @Test
    void addReferral_savesAndReturnsReferral() {
        AmbReferralModel referral = new AmbReferralModel();
        referral.setReferralCode("REF123");
        referral.setAmbassadorId("AMB001");

        when(referralRepository.save(any(AmbReferralModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AmbReferralModel result = referralService.addReferral(referral);

        assertNotNull(result);
        assertEquals("REF123", result.getReferralCode());
        assertEquals("AMB001", result.getAmbassadorId());
        verify(referralRepository).save(referral);
    }

    @Test
    void getReferral_returnsListOfReferralsForCode() {
        String referralCode = "REF123";
        List<AmbReferralModel> referrals = List.of(new AmbReferralModel(), new AmbReferralModel());

        when(referralRepository.findAllByReferralCode(referralCode)).thenReturn(referrals);

        List<AmbReferralModel> result = referralService.getReferral(referralCode);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(referralRepository).findAllByReferralCode(referralCode);
    }

    @Test
    void getReferralByAmbassador_returnsListOfReferralsForAmbassador() {
        String ambassadorId = "AMB001";
        List<AmbReferralModel> referrals = List.of(new AmbReferralModel(), new AmbReferralModel());

        when(referralRepository.findAllByAmbassadorId(ambassadorId)).thenReturn(referrals);

        List<AmbReferralModel> result = referralService.getReferralByAmbassador(ambassadorId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(referralRepository).findAllByAmbassadorId(ambassadorId);
    }

    @Test
    void updateReferral_updatesAndReturnsUpdatedReferral() {
        String id = "REF001";
        AmbReferralModel existingReferral = new AmbReferralModel();
        existingReferral.setId(id);
        existingReferral.setReferralCode("OLD_CODE");

        AmbReferralModel updatedReferral = new AmbReferralModel();
        updatedReferral.setReferralCode("NEW_CODE");

        when(referralRepository.findById(id)).thenReturn(Optional.of(existingReferral));
        when(referralRepository.save(any(AmbReferralModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AmbReferralModel result = referralService.updateReferral(id, updatedReferral);

        assertNotNull(result);
        assertEquals("NEW_CODE", result.getReferralCode());
        verify(referralRepository).save(existingReferral);
    }

    @Test
    void updateReferral_returnsNullWhenReferralDoesNotExist() {
        String id = "REF001";
        AmbReferralModel updatedReferral = new AmbReferralModel();

        when(referralRepository.findById(id)).thenReturn(Optional.empty());

        AmbReferralModel result = referralService.updateReferral(id, updatedReferral);

        assertNull(result);
        verify(referralRepository, never()).save(any(AmbReferralModel.class));
    }
}
