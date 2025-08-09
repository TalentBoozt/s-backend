package com.talentboozt.s_backend.domains.com_job_portal.controller;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.talentboozt.s_backend.domains.com_job_portal.model.CompanyModel;
import com.talentboozt.s_backend.domains.com_job_portal.service.CompanyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CompanyControllerTest {

    @Mock
    private CompanyService companyService;

    @InjectMocks
    private CompanyController companyController;

    private CompanyModel mockCompany;

    @BeforeEach
    void setUp() {
        mockCompany = new CompanyModel();
        mockCompany.setId("company1");
    }

    @Test
    void getAllCompanies_returnsAllCompanies() {
        when(companyService.getAllCompanies()).thenReturn(List.of(mockCompany));

        List<CompanyModel> result = companyController.getAllCompanies();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("company1", result.get(0).getId());
        verify(companyService).getAllCompanies();
    }

    @Test
    void getAllCompanies_paginated_returnsCompanies() {
        when(companyService.getCompaniesPaginated(0, 10)).thenReturn(List.of(mockCompany));

        List<CompanyModel> result = companyController.getAllCompanies(0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("company1", result.get(0).getId());
        verify(companyService).getCompaniesPaginated(0, 10);
    }

    @Test
    void getCompany_returnsCompanyById() {
        when(companyService.getCompany("company1")).thenReturn(mockCompany);

        CompanyModel result = companyController.getCompany("company1");

        assertNotNull(result);
        assertEquals("company1", result.getId());
        verify(companyService).getCompany("company1");
    }

    @Test
    void getCompanyByType_returnsCompaniesByType() {
        when(companyService.getCompanyByType("tech")).thenReturn(Optional.of(List.of(mockCompany)));

        Optional<List<CompanyModel>> result = companyController.getCompanyByType("tech");

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
        assertEquals("company1", result.get().get(0).getId());
        verify(companyService).getCompanyByType("tech");
    }

    @Test
    void getCompanyByType_noCompanies_returnsEmptyOptional() {
        when(companyService.getCompanyByType("tech")).thenReturn(Optional.empty());

        Optional<List<CompanyModel>> result = companyController.getCompanyByType("tech");

        assertTrue(result.isEmpty());
        verify(companyService).getCompanyByType("tech");
    }

    @Test
    void addCompany_addsAndReturnsCompany() {
        when(companyService.addCompany(mockCompany)).thenReturn(mockCompany);

        CompanyModel result = companyController.addCompany(mockCompany);

        assertNotNull(result);
        assertEquals("company1", result.getId());
        verify(companyService).addCompany(mockCompany);
    }

    @Test
    void updateLogoPic_updatesAndReturnsCompany() {
        when(companyService.updateLogoPic(mockCompany)).thenReturn(mockCompany);

        CompanyModel result = companyController.updateLogoPic(mockCompany);

        assertNotNull(result);
        assertEquals("company1", result.getId());
        verify(companyService).updateLogoPic(mockCompany);
    }

    @Test
    void updateCoverPic_updatesAndReturnsCompany() {
        when(companyService.updateCoverPic(mockCompany)).thenReturn(mockCompany);

        CompanyModel result = companyController.updateCoverPic(mockCompany);

        assertNotNull(result);
        assertEquals("company1", result.getId());
        verify(companyService).updateCoverPic(mockCompany);
    }

    @Test
    void updateThumb1Pic_updatesAndReturnsCompany() {
        when(companyService.updateThumb1Pic(mockCompany)).thenReturn(mockCompany);

        CompanyModel result = companyController.updateThumb1Pic(mockCompany);

        assertNotNull(result);
        assertEquals("company1", result.getId());
        verify(companyService).updateThumb1Pic(mockCompany);
    }

    @Test
    void updateThumb2Pic_updatesAndReturnsCompany() {
        when(companyService.updateThumb2Pic(mockCompany)).thenReturn(mockCompany);

        CompanyModel result = companyController.updateThumb2Pic(mockCompany);

        assertNotNull(result);
        assertEquals("company1", result.getId());
        verify(companyService).updateThumb2Pic(mockCompany);
    }

    @Test
    void updateThumb3Pic_updatesAndReturnsCompany() {
        when(companyService.updateThumb3Pic(mockCompany)).thenReturn(mockCompany);

        CompanyModel result = companyController.updateThumb3Pic(mockCompany);

        assertNotNull(result);
        assertEquals("company1", result.getId());
        verify(companyService).updateThumb3Pic(mockCompany);
    }

    @Test
    void updateCompany_updatesAndReturnsCompany() {
        when(companyService.updateCompany(mockCompany)).thenReturn(mockCompany);

        CompanyModel result = companyController.updateCompany(mockCompany);

        assertNotNull(result);
        assertEquals("company1", result.getId());
        verify(companyService).updateCompany(mockCompany);
    }

    @Test
    void updateNotifications_updatesAndReturnsCompany() {
        when(companyService.updateNotifications(mockCompany)).thenReturn(mockCompany);

        CompanyModel result = companyController.updateNotifications(mockCompany);

        assertNotNull(result);
        assertEquals("company1", result.getId());
        verify(companyService).updateNotifications(mockCompany);
    }

    @Test
    void deleteCompany_deletesCompany() {
        companyController.deleteCompany("company1");

        verify(companyService).deleteCompany("company1");
    }
}
