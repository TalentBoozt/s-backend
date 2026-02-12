package com.talentboozt.s_backend.domains.com_job_portal.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.talentboozt.s_backend.domains.com_job_portal.model.CmpSocialModel;
import com.talentboozt.s_backend.domains.com_job_portal.model.CompanyModel;
import com.talentboozt.s_backend.domains.com_job_portal.repository.mongodb.CmpSocialRepository;
import com.talentboozt.s_backend.domains.com_job_portal.repository.mongodb.CompanyRepository;
import com.talentboozt.s_backend.domains.common.dto.SocialLinksDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
class CmpSocialServiceTest {

    @Mock
    private CmpSocialRepository cmpSocialRepository;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CmpSocialService cmpSocialService;

    private CmpSocialModel mockSocialModel;
    private SocialLinksDTO mockSocialDTO;
    private CompanyModel mockCompany;

    @BeforeEach
    void setUp() {
        mockSocialModel = new CmpSocialModel();
        mockSocialModel.setId("social1");
        mockSocialModel.setCompanyId("company1");

        mockSocialDTO = new SocialLinksDTO();
        mockSocialDTO.setId("link1");
        mockSocialModel.setSocialLinks(new ArrayList<>(List.of(mockSocialDTO)));

        mockCompany = new CompanyModel();
        mockCompany.setId("company1");
        mockCompany.setProfileCompleted(new HashMap<>());
    }

    @Test
    void getCmpSocialsByCompanyId_returnsSocialsList() {
        when(cmpSocialRepository.findByCompanyId("company1")).thenReturn(List.of(mockSocialModel));

        List<CmpSocialModel> result = cmpSocialService.getCmpSocialsByCompanyId("company1");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("social1", result.get(0).getId());
        verify(cmpSocialRepository).findByCompanyId("company1");
    }

    @Test
    void addCmpSocials_existingSocials_updatesAndReturns() {
        when(cmpSocialRepository.findByCompanyId("company1")).thenReturn(List.of(mockSocialModel));
        when(cmpSocialRepository.save(mockSocialModel)).thenReturn(mockSocialModel);
        when(companyRepository.findById("company1")).thenReturn(Optional.of(mockCompany));
        when(companyRepository.save(mockCompany)).thenReturn(mockCompany);

        CmpSocialModel newSocialModel = new CmpSocialModel();
        newSocialModel.setCompanyId("company1");
        SocialLinksDTO newSocialDTO = new SocialLinksDTO();
        newSocialDTO.setId("link2");
        newSocialModel.setSocialLinks(List.of(newSocialDTO));

        CmpSocialModel result = cmpSocialService.addCmpSocials(newSocialModel);

        assertNotNull(result);
        assertEquals("social1", result.getId());
        assertEquals(1, result.getSocialLinks().size());
        assertEquals("link2", result.getSocialLinks().get(0).getId());
        assertEquals("social1", mockCompany.getSocialLinks());
        Object profileCompleted = mockCompany.getProfileCompleted();
        if (profileCompleted instanceof Map) {
            assertTrue((boolean) ((Map<?, ?>) profileCompleted).get("cmpSocial"));
        }
        verify(cmpSocialRepository).findByCompanyId("company1");
        verify(cmpSocialRepository).save(mockSocialModel);
        verify(companyRepository).findById("company1");
        verify(companyRepository).save(mockCompany);
    }

    @Test
    void addCmpSocials_noExistingSocials_createsNew() {
        when(cmpSocialRepository.findByCompanyId("company1")).thenReturn(Collections.emptyList());
        when(cmpSocialRepository.save(mockSocialModel)).thenReturn(mockSocialModel);
        when(companyRepository.findById("company1")).thenReturn(Optional.of(mockCompany));
        when(companyRepository.save(mockCompany)).thenReturn(mockCompany);

        CmpSocialModel result = cmpSocialService.addCmpSocials(mockSocialModel);

        assertNotNull(result);
        assertEquals("social1", result.getId());
        assertEquals("social1", mockCompany.getSocialLinks());
        Object profileCompleted = mockCompany.getProfileCompleted();
        if (profileCompleted instanceof Map) {
            assertTrue((boolean) ((Map<?, ?>) profileCompleted).get("cmpSocial"));
        }
        verify(cmpSocialRepository).findByCompanyId("company1");
        verify(cmpSocialRepository, times(2)).save(mockSocialModel);
        verify(companyRepository).findById("company1");
        verify(companyRepository).save(mockCompany);
    }

    @Test
    void updateCmpSocials_updatesSocialModel() {
        when(cmpSocialRepository.findById("social1")).thenReturn(Optional.of(mockSocialModel));
        when(cmpSocialRepository.save(mockSocialModel)).thenReturn(mockSocialModel);

        CmpSocialModel updatedSocial = new CmpSocialModel();
        updatedSocial.setCompanyId("company1");
        updatedSocial.setSocialLinks(List.of(new SocialLinksDTO()));

        CmpSocialModel result = cmpSocialService.updateCmpSocials("social1", updatedSocial);

        assertNotNull(result);
        assertEquals("social1", result.getId());
        verify(cmpSocialRepository).findById("social1");
        verify(cmpSocialRepository).save(mockSocialModel);
    }

    @Test
    void updateCmpSocials_socialNotFound_returnsNull() {
        when(cmpSocialRepository.findById("social1")).thenReturn(Optional.empty());

        CmpSocialModel result = cmpSocialService.updateCmpSocials("social1", mockSocialModel);

        assertNull(result);
        verify(cmpSocialRepository).findById("social1");
        verify(cmpSocialRepository, never()).save(any());
    }

    @Test
    void editCmpSocial_updatesSingleSocialLink() {
        SocialLinksDTO updatedLink = new SocialLinksDTO();
        updatedLink.setId("link1");
        updatedLink.setFacebook("new_facebook");
        when(cmpSocialRepository.findByCompanyId("company1")).thenReturn(List.of(mockSocialModel));
        when(cmpSocialRepository.save(mockSocialModel)).thenReturn(mockSocialModel);

        CmpSocialModel result = cmpSocialService.editCmpSocial("company1", updatedLink);

        assertNotNull(result);
        assertEquals("social1", result.getId());
        assertEquals("new_facebook", result.getSocialLinks().get(0).getFacebook());
        verify(cmpSocialRepository).findByCompanyId("company1");
        verify(cmpSocialRepository).save(mockSocialModel);
    }

    @Test
    void editCmpSocial_noSocialsForCompany_throwsException() {
        when(cmpSocialRepository.findByCompanyId("company1")).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> cmpSocialService.editCmpSocial("company1", mockSocialDTO));

        assertEquals("CmpSocials not found for companyId: company1", exception.getMessage());
        verify(cmpSocialRepository).findByCompanyId("company1");
        verify(cmpSocialRepository, never()).save(any());
    }

    @Test
    void deleteCmpSocials_deletesByCompanyId() {
        cmpSocialService.deleteCmpSocials("company1");

        verify(cmpSocialRepository).deleteByCompanyId("company1");
    }

    @Test
    void getCmpSocialsByCompanyIdAsync_returnsSocialsList() throws Exception {
        when(cmpSocialRepository.findByCompanyId("company1")).thenReturn(List.of(mockSocialModel));

        CompletableFuture<List<CmpSocialModel>> result = cmpSocialService.getCmpSocialsByCompanyIdAsync("company1");

        assertNotNull(result);
        assertEquals(1, result.get().size());
        assertEquals("social1", result.get().get(0).getId());
        verify(cmpSocialRepository).findByCompanyId("company1");
    }
}
