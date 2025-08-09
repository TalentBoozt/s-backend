package com.talentboozt.s_backend.domains.com_job_portal.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.talentboozt.s_backend.domains.com_job_portal.model.CmpSocialModel;
import com.talentboozt.s_backend.domains.com_job_portal.service.CmpSocialService;
import com.talentboozt.s_backend.domains.common.dto.SocialLinksDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class CmpSocialControllerTest {

    @Mock
    private CmpSocialService cmpSocialService;

    @InjectMocks
    private CmpSocialController cmpSocialController;

    private CmpSocialModel mockSocial;
    private SocialLinksDTO mockSocialDTO;

    @BeforeEach
    void setUp() {
        mockSocial = new CmpSocialModel();
        mockSocial.setId("social1");
        mockSocial.setCompanyId("company1");

        mockSocialDTO = new SocialLinksDTO();
        mockSocialDTO.setId("social1");
    }

    @Test
    void getCmpSocialsByCompanyId_returnsSocialsList() {
        when(cmpSocialService.getCmpSocialsByCompanyId("company1")).thenReturn(List.of(mockSocial));

        List<CmpSocialModel> result = cmpSocialController.getCmpSocialsByCompanyId("company1");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("social1", result.get(0).getId());
        verify(cmpSocialService).getCmpSocialsByCompanyId("company1");
    }

    @Test
    void addCmpSocials_addsAndReturnsSocial() {
        when(cmpSocialService.addCmpSocials(mockSocial)).thenReturn(mockSocial);

        CmpSocialModel result = cmpSocialController.addCmpSocials(mockSocial);

        assertNotNull(result);
        assertEquals("social1", result.getId());
        verify(cmpSocialService).addCmpSocials(mockSocial);
    }

    @Test
    void updateCmpSocials_updatesAndReturnsSocial() {
        when(cmpSocialService.updateCmpSocials("social1", mockSocial)).thenReturn(mockSocial);

        CmpSocialModel result = cmpSocialController.updateCmpSocials("social1", mockSocial);

        assertNotNull(result);
        assertEquals("social1", result.getId());
        verify(cmpSocialService).updateCmpSocials("social1", mockSocial);
    }

    @Test
    void updateCmpSocial_updatesSingleSocial() {
        when(cmpSocialService.editCmpSocial("company1", mockSocialDTO)).thenReturn(mockSocial);

        CmpSocialModel result = cmpSocialController.updateCmpSocial("company1", mockSocialDTO);

        assertNotNull(result);
        assertEquals("social1", result.getId());
        verify(cmpSocialService).editCmpSocial("company1", mockSocialDTO);
    }

    @Test
    void deleteCmpSocials_deletesSocials() {
        cmpSocialController.deleteCmpSocials("company1");

        verify(cmpSocialService).deleteCmpSocials("company1");
    }
}
