package com.talentboozt.s_backend.domains.com_job_portal.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.talentboozt.s_backend.domains.com_job_portal.model.CompanyModel;
import com.talentboozt.s_backend.domains.com_job_portal.repository.mongodb.CompanyRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyService companyService;

    private CompanyModel mockCompany;

    @BeforeEach
    void setUp() {
        mockCompany = new CompanyModel();
        mockCompany.setId("company1");
        mockCompany.setProfileCompleted(new HashMap<>());
    }

    @Test
    void getAllCompanies_returnsAllCompanies() {
        when(companyRepository.findAll()).thenReturn(List.of(mockCompany));

        List<CompanyModel> result = companyService.getAllCompanies();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("company1", result.get(0).getId());
        verify(companyRepository).findAll();
    }

    @Test
    void getCompaniesPaginated_returnsPaginatedCompanies() {
        when(companyRepository.findAll()).thenReturn(List.of(mockCompany));

        List<CompanyModel> result = companyService.getCompaniesPaginated(0, 1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("company1", result.get(0).getId());
        verify(companyRepository).findAll();
    }

    @Test
    void getCompany_returnsCompanyById() {
        when(companyRepository.findById("company1")).thenReturn(Optional.of(mockCompany));

        CompanyModel result = companyService.getCompany("company1");

        assertNotNull(result);
        assertEquals("company1", result.getId());
        verify(companyRepository).findById("company1");
    }

    @Test
    void getCompany_companyNotFound_returnsNull() {
        when(companyRepository.findById("company1")).thenReturn(Optional.empty());

        CompanyModel result = companyService.getCompany("company1");

        assertNull(result);
        verify(companyRepository).findById("company1");
    }

    @Test
    void getCompanyByType_returnsCompaniesByType() {
        when(companyRepository.findAllByCompanyType("tech")).thenReturn(Optional.of(List.of(mockCompany)));

        Optional<List<CompanyModel>> result = companyService.getCompanyByType("tech");

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
        assertEquals("company1", result.get().get(0).getId());
        verify(companyRepository).findAllByCompanyType("tech");
    }

    @Test
    void getCompanyByType_noCompanies_returnsEmptyOptional() {
        when(companyRepository.findAllByCompanyType("tech")).thenReturn(Optional.empty());

        Optional<List<CompanyModel>> result = companyService.getCompanyByType("tech");

        assertTrue(result.isEmpty());
        verify(companyRepository).findAllByCompanyType("tech");
    }

    @Test
    void addCompany_addsAndReturnsCompany() {
        when(companyRepository.save(mockCompany)).thenReturn(mockCompany);

        CompanyModel result = companyService.addCompany(mockCompany);

        assertNotNull(result);
        assertEquals("company1", result.getId());
        verify(companyRepository).save(mockCompany);
    }

    @Test
    void updateLogoPic_updatesLogoAndProfileCompleted() {
        mockCompany.setLogo("logo.png");
        when(companyRepository.findById("company1")).thenReturn(Optional.of(mockCompany));
        when(companyRepository.save(mockCompany)).thenReturn(mockCompany);

        CompanyModel result = companyService.updateLogoPic(mockCompany);

        assertNotNull(result);
        assertEquals("logo.png", result.getLogo());
        Object profileCompleted = mockCompany.getProfileCompleted();
        if (profileCompleted instanceof Map) {
            assertTrue((boolean) ((Map<?, ?>) profileCompleted).get("logo"));
        }
        verify(companyRepository).findById("company1");
        verify(companyRepository).save(mockCompany);
    }

    @Test
    void updateLogoPic_companyNotFound_returnsNull() {
        when(companyRepository.findById("company1")).thenReturn(Optional.empty());

        CompanyModel result = companyService.updateLogoPic(mockCompany);

        assertNull(result);
        verify(companyRepository).findById("company1");
        verify(companyRepository, never()).save(any());
    }

    @Test
    void updateCoverPic_updatesCoverAndProfileCompleted() {
        mockCompany.setProfileBanner("cover.png");
        when(companyRepository.findById("company1")).thenReturn(Optional.of(mockCompany));
        when(companyRepository.save(mockCompany)).thenReturn(mockCompany);

        CompanyModel result = companyService.updateCoverPic(mockCompany);

        assertNotNull(result);
        assertEquals("cover.png", result.getProfileBanner());
        Object profileCompleted = mockCompany.getProfileCompleted();
        if (profileCompleted instanceof Map) {
            assertTrue((boolean) ((Map<?, ?>) profileCompleted).get("coverPic"));
        }
        verify(companyRepository).findById("company1");
        verify(companyRepository).save(mockCompany);
    }

    @Test
    void updateCoverPic_companyNotFound_returnsNull() {
        when(companyRepository.findById("company1")).thenReturn(Optional.empty());

        CompanyModel result = companyService.updateCoverPic(mockCompany);

        assertNull(result);
        verify(companyRepository).findById("company1");
        verify(companyRepository, never()).save(any());
    }

    @Test
    void updateThumb1Pic_updatesImage1AndProfileCompleted() {
        mockCompany.setImage1("image1.png");
        when(companyRepository.findById("company1")).thenReturn(Optional.of(mockCompany));
        when(companyRepository.save(mockCompany)).thenReturn(mockCompany);

        CompanyModel result = companyService.updateThumb1Pic(mockCompany);

        assertNotNull(result);
        assertEquals("image1.png", result.getImage1());
        Object profileCompleted = mockCompany.getProfileCompleted();
        if (profileCompleted instanceof Map) {
            assertTrue((boolean) ((Map<?, ?>) profileCompleted).get("image1"));
        }
        verify(companyRepository).findById("company1");
        verify(companyRepository).save(mockCompany);
    }

    @Test
    void updateThumb1Pic_companyNotFound_returnsNull() {
        when(companyRepository.findById("company1")).thenReturn(Optional.empty());

        CompanyModel result = companyService.updateThumb1Pic(mockCompany);

        assertNull(result);
        verify(companyRepository).findById("company1");
        verify(companyRepository, never()).save(any());
    }

    @Test
    void updateThumb2Pic_updatesImage2AndProfileCompleted() {
        mockCompany.setImage2("image2.png");
        when(companyRepository.findById("company1")).thenReturn(Optional.of(mockCompany));
        when(companyRepository.save(mockCompany)).thenReturn(mockCompany);

        CompanyModel result = companyService.updateThumb2Pic(mockCompany);

        assertNotNull(result);
        assertEquals("image2.png", result.getImage2());
        Object profileCompleted = mockCompany.getProfileCompleted();
        if (profileCompleted instanceof Map) {
            assertTrue((boolean) ((Map<?, ?>) profileCompleted).get("image2"));
        }
        verify(companyRepository).findById("company1");
        verify(companyRepository).save(mockCompany);
    }

    @Test
    void updateThumb2Pic_companyNotFound_returnsNull() {
        when(companyRepository.findById("company1")).thenReturn(Optional.empty());

        CompanyModel result = companyService.updateThumb2Pic(mockCompany);

        assertNull(result);
        verify(companyRepository).findById("company1");
        verify(companyRepository, never()).save(any());
    }

    @Test
    void updateThumb3Pic_updatesImage3AndProfileCompleted() {
        mockCompany.setImage3("image3.png");
        when(companyRepository.findById("company1")).thenReturn(Optional.of(mockCompany));
        when(companyRepository.save(mockCompany)).thenReturn(mockCompany);

        CompanyModel result = companyService.updateThumb3Pic(mockCompany);

        assertNotNull(result);
        assertEquals("image3.png", result.getImage3());
        Object profileCompleted = mockCompany.getProfileCompleted();
        if (profileCompleted instanceof Map) {
            assertTrue((boolean) ((Map<?, ?>) profileCompleted).get("image3"));
        }
        verify(companyRepository).findById("company1");
        verify(companyRepository).save(mockCompany);
    }

    @Test
    void updateThumb3Pic_companyNotFound_returnsNull() {
        when(companyRepository.findById("company1")).thenReturn(Optional.empty());

        CompanyModel result = companyService.updateThumb3Pic(mockCompany);

        assertNull(result);
        verify(companyRepository).findById("company1");
        verify(companyRepository, never()).save(any());
    }

    @Test
    void updateCompany_updatesFieldsAndProfileCompleted() {
        mockCompany.setName("Test Company");
        mockCompany.setContactEmail("test@example.com");
        mockCompany.setCompanyStory("Story");
        mockCompany.setFounderName("Founder");
        mockCompany.setFoundedDate("2020-01-01");
        mockCompany.setLocation("NY");
        mockCompany.setNumberOfEmployees("100");
        mockCompany.setWebsite("www.example.com");

        when(companyRepository.findById("company1")).thenReturn(Optional.of(mockCompany));
        when(companyRepository.save(mockCompany)).thenReturn(mockCompany);

        CompanyModel result = companyService.updateCompany(mockCompany);

        assertNotNull(result);
        assertEquals("Test Company", result.getName());
        Object profileCompleted = mockCompany.getProfileCompleted();
        if (profileCompleted instanceof Map) {
            assertTrue((boolean) ((Map<?, ?>) profileCompleted).get("name"));
            assertTrue((boolean) ((Map<?, ?>) profileCompleted).get("email"));
            assertTrue((boolean) ((Map<?, ?>) profileCompleted).get("story"));
            assertTrue((boolean) ((Map<?, ?>) profileCompleted).get("founderName"));
            assertTrue((boolean) ((Map<?, ?>) profileCompleted).get("foundedDate"));
            assertTrue((boolean) ((Map<?, ?>) profileCompleted).get("location"));
            assertTrue((boolean) ((Map<?, ?>) profileCompleted).get("numberOfEmployees"));
            assertTrue((boolean) ((Map<?, ?>) profileCompleted).get("website"));
        }
        verify(companyRepository).findById("company1");
        verify(companyRepository).save(mockCompany);
    }

    @Test
    void updateCompany_companyNotFound_returnsNull() {
        when(companyRepository.findById("company1")).thenReturn(Optional.empty());

        CompanyModel result = companyService.updateCompany(mockCompany);

        assertNull(result);
        verify(companyRepository).findById("company1");
        verify(companyRepository, never()).save(any());
    }

    @Test
    void updateNotifications_updatesNotifications() {
        Map<String, String> notifications = new HashMap<>();
        notifications.put("email", "enabled");
        mockCompany.setAccountNotifications(notifications);
        mockCompany.setMarketingNotifications(notifications);
        when(companyRepository.findById("company1")).thenReturn(Optional.of(mockCompany));
        when(companyRepository.save(mockCompany)).thenReturn(mockCompany);

        CompanyModel result = companyService.updateNotifications(mockCompany);

        assertNotNull(result);
        assertEquals(notifications, result.getAccountNotifications());
        assertEquals(notifications, result.getMarketingNotifications());
        verify(companyRepository).findById("company1");
        verify(companyRepository).save(mockCompany);
    }

    @Test
    void updateNotifications_companyNotFound_returnsNull() {
        when(companyRepository.findById("company1")).thenReturn(Optional.empty());

        CompanyModel result = companyService.updateNotifications(mockCompany);

        assertNull(result);
        verify(companyRepository).findById("company1");
        verify(companyRepository, never()).save(any());
    }

    @Test
    void deleteCompany_deletesById() {
        companyService.deleteCompany("company1");

        verify(companyRepository).deleteById("company1");
    }

    @Test
    void getAllCompaniesAsync_returnsCompanies() throws Exception {
        when(companyRepository.findAll()).thenReturn(List.of(mockCompany));

        CompletableFuture<List<CompanyModel>> result = companyService.getAllCompaniesAsync();

        assertNotNull(result);
        assertEquals(1, result.get().size());
        assertEquals("company1", result.get().get(0).getId());
        verify(companyRepository).findAll();
    }

    @Test
    void getCompanyByIdAsync_returnsCompany() throws Exception {
        when(companyRepository.findById("company1")).thenReturn(Optional.of(mockCompany));

        CompletableFuture<CompanyModel> result = companyService.getCompanyByIdAsync("company1");

        assertNotNull(result);
        assertEquals("company1", result.get().getId());
        verify(companyRepository).findById("company1");
    }

    @Test
    void getCompanyByIdAsync_companyNotFound_returnsNull() throws Exception {
        when(companyRepository.findById("company1")).thenReturn(Optional.empty());

        CompletableFuture<CompanyModel> result = companyService.getCompanyByIdAsync("company1");

        assertNotNull(result);
        assertNull(result.get());
        verify(companyRepository).findById("company1");
    }

    @Test
    void findAndUpdateCompanyLevel_updatesLevel() {
        mockCompany.setCompanyLevel("Basic");
        when(companyRepository.findById("company1")).thenReturn(Optional.of(mockCompany));
        when(companyRepository.save(mockCompany)).thenReturn(mockCompany);

        companyService.findAndUpdateCompanyLevel("company1", "Premium");

        assertEquals("Premium", mockCompany.getCompanyLevel());
        verify(companyRepository).findById("company1");
        verify(companyRepository).save(mockCompany);
    }

    @Test
    void findAndUpdateCompanyLevel_companyNotFound_doesNothing() {
        when(companyRepository.findById("company1")).thenReturn(Optional.empty());

        companyService.findAndUpdateCompanyLevel("company1", "Premium");

        verify(companyRepository).findById("company1");
        verify(companyRepository, never()).save(any());
    }
}
