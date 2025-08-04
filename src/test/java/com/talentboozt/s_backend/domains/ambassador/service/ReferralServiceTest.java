package com.talentboozt.s_backend.domains.ambassador.service;

import com.talentboozt.s_backend.domains.ambassador.model.ReferralModel;
import com.talentboozt.s_backend.domains.ambassador.repository.ReferralRepository;
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
    private ReferralRepository referralRepository;

    @InjectMocks
    private ReferralService referralService;

    @Test
    void addReferral_savesAndReturnsReferral() {
        ReferralModel referral = new ReferralModel();
        referral.setReferralCode("REF123");
        referral.setAmbassadorId("AMB001");

        when(referralRepository.save(any(ReferralModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReferralModel result = referralService.addReferral(referral);

        assertNotNull(result);
        assertEquals("REF123", result.getReferralCode());
        assertEquals("AMB001", result.getAmbassadorId());
        verify(referralRepository).save(referral);
    }

    @Test
    void getReferral_returnsListOfReferralsForCode() {
        String referralCode = "REF123";
        List<ReferralModel> referrals = List.of(new ReferralModel(), new ReferralModel());

        when(referralRepository.findAllByReferralCode(referralCode)).thenReturn(referrals);

        List<ReferralModel> result = referralService.getReferral(referralCode);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(referralRepository).findAllByReferralCode(referralCode);
    }

    @Test
    void getReferralByAmbassador_returnsListOfReferralsForAmbassador() {
        String ambassadorId = "AMB001";
        List<ReferralModel> referrals = List.of(new ReferralModel(), new ReferralModel());

        when(referralRepository.findAllByAmbassadorId(ambassadorId)).thenReturn(referrals);

        List<ReferralModel> result = referralService.getReferralByAmbassador(ambassadorId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(referralRepository).findAllByAmbassadorId(ambassadorId);
    }

    @Test
    void updateReferral_updatesAndReturnsUpdatedReferral() {
        String id = "REF001";
        ReferralModel existingReferral = new ReferralModel();
        existingReferral.setId(id);
        existingReferral.setReferralCode("OLD_CODE");

        ReferralModel updatedReferral = new ReferralModel();
        updatedReferral.setReferralCode("NEW_CODE");

        when(referralRepository.findById(id)).thenReturn(Optional.of(existingReferral));
        when(referralRepository.save(any(ReferralModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReferralModel result = referralService.updateReferral(id, updatedReferral);

        assertNotNull(result);
        assertEquals("NEW_CODE", result.getReferralCode());
        verify(referralRepository).save(existingReferral);
    }

    @Test
    void updateReferral_returnsNullWhenReferralDoesNotExist() {
        String id = "REF001";
        ReferralModel updatedReferral = new ReferralModel();

        when(referralRepository.findById(id)).thenReturn(Optional.empty());

        ReferralModel result = referralService.updateReferral(id, updatedReferral);

        assertNull(result);
        verify(referralRepository, never()).save(any(ReferralModel.class));
    }
}
