package com.talentboozt.s_backend.domains.com_job_portal.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.talentboozt.s_backend.domains.com_job_portal.dto.PostedJobsDTO;
import com.talentboozt.s_backend.domains.com_job_portal.model.CmpPostedJobsModel;
import com.talentboozt.s_backend.domains.com_job_portal.model.CompanyModel;
import com.talentboozt.s_backend.domains.com_job_portal.repository.CmpPostedJobsRepository;
import com.talentboozt.s_backend.domains.com_job_portal.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
class CmpPostedJobsServiceTest {

    @Mock
    private CmpPostedJobsRepository cmpPostedJobsRepository;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CmpPostedJobsService cmpPostedJobsService;

    private CmpPostedJobsModel mockJobModel;
    private PostedJobsDTO mockJobDTO;
    private CompanyModel mockCompany;

    @BeforeEach
    void setUp() {
        mockJobModel = new CmpPostedJobsModel();
        mockJobModel.setId("jobModel1");
        mockJobModel.setCompanyId("company1");

        mockJobDTO = new PostedJobsDTO();
        mockJobDTO.setId("job1");
        mockJobModel.setPostedJobs(new ArrayList<>(List.of(mockJobDTO)));

        mockCompany = new CompanyModel();
        mockCompany.setId("company1");
    }

    @Test
    void getCmpPostedJobsByCompanyId_returnsJobsList() {
        when(cmpPostedJobsRepository.findByCompanyId("company1")).thenReturn(List.of(mockJobModel));

        List<CmpPostedJobsModel> result = cmpPostedJobsService.getCmpPostedJobsByCompanyId("company1");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("jobModel1", result.get(0).getId());
        verify(cmpPostedJobsRepository).findByCompanyId("company1");
    }

    @Test
    void getAllCmpPostedJobs_returnsAllJobs() {
        when(cmpPostedJobsRepository.findAll()).thenReturn(List.of(mockJobModel));

        List<CmpPostedJobsModel> result = cmpPostedJobsService.getAllCmpPostedJobs();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("jobModel1", result.get(0).getId());
        verify(cmpPostedJobsRepository).findAll();
    }

    @Test
    void addCmpPostedJobs_existingJobs_appendsAndUpdates() {
        when(cmpPostedJobsRepository.findByCompanyId("company1")).thenReturn(List.of(mockJobModel));
        when(cmpPostedJobsRepository.save(mockJobModel)).thenReturn(mockJobModel);
        when(companyRepository.findById("company1")).thenReturn(Optional.of(mockCompany));
        when(companyRepository.save(mockCompany)).thenReturn(mockCompany);

        CmpPostedJobsModel newJobModel = new CmpPostedJobsModel();
        newJobModel.setCompanyId("company1");
        PostedJobsDTO newJobDTO = new PostedJobsDTO();
        newJobDTO.setId("job2");
        newJobModel.setPostedJobs(List.of(newJobDTO));

        CmpPostedJobsModel result = cmpPostedJobsService.addCmpPostedJobs(newJobModel);

        assertNotNull(result);
        assertEquals("jobModel1", result.getId());
        assertEquals(2, result.getPostedJobs().size());
        assertEquals("jobModel1", mockCompany.getPostedJobs());
        verify(cmpPostedJobsRepository).findByCompanyId("company1");
        verify(cmpPostedJobsRepository).save(mockJobModel);
        verify(companyRepository).findById("company1");
        verify(companyRepository).save(mockCompany);
    }

    @Test
    void addCmpPostedJobs_noExistingJobs_createsNew() {
        when(cmpPostedJobsRepository.findByCompanyId("company1")).thenReturn(Collections.emptyList());
        when(cmpPostedJobsRepository.save(mockJobModel)).thenReturn(mockJobModel);
        when(companyRepository.findById("company1")).thenReturn(Optional.of(mockCompany));
        when(companyRepository.save(mockCompany)).thenReturn(mockCompany);

        CmpPostedJobsModel result = cmpPostedJobsService.addCmpPostedJobs(mockJobModel);

        assertNotNull(result);
        assertEquals("jobModel1", result.getId());
        assertEquals("jobModel1", mockCompany.getPostedJobs());
        verify(cmpPostedJobsRepository).findByCompanyId("company1");
        verify(cmpPostedJobsRepository, times(2)).save(mockJobModel);
        verify(companyRepository).findById("company1");
        verify(companyRepository).save(mockCompany);
    }

    @Test
    void getPostedJobByJobId_returnsJobDTO() {
        when(cmpPostedJobsRepository.findByCompanyId("company1")).thenReturn(List.of(mockJobModel));

        PostedJobsDTO result = cmpPostedJobsService.getPostedJobByJobId("company1", "job1");

        assertNotNull(result);
        assertEquals("job1", result.getId());
        verify(cmpPostedJobsRepository).findByCompanyId("company1");
    }

    @Test
    void getPostedJobByJobId_jobNotFound_returnsNull() {
        when(cmpPostedJobsRepository.findByCompanyId("company1")).thenReturn(List.of(mockJobModel));

        PostedJobsDTO result = cmpPostedJobsService.getPostedJobByJobId("company1", "job2");

        assertNull(result);
        verify(cmpPostedJobsRepository).findByCompanyId("company1");
    }

    @Test
    void getPostedJobByJobId_noJobsForCompany_returnsNull() {
        when(cmpPostedJobsRepository.findByCompanyId("company1")).thenReturn(Collections.emptyList());

        PostedJobsDTO result = cmpPostedJobsService.getPostedJobByJobId("company1", "job1");

        assertNull(result);
        verify(cmpPostedJobsRepository).findByCompanyId("company1");
    }

    @Test
    void updatePostedJob_updatesJobDTO() {
        PostedJobsDTO updatedJob = new PostedJobsDTO();
        updatedJob.setId("job1");
        when(cmpPostedJobsRepository.findByCompanyId("company1")).thenReturn(List.of(mockJobModel));
        when(cmpPostedJobsRepository.save(mockJobModel)).thenReturn(mockJobModel);

        PostedJobsDTO result = cmpPostedJobsService.updatePostedJob("company1", "job1", updatedJob);

        assertNotNull(result);
        assertEquals("job1", result.getId());
        verify(cmpPostedJobsRepository).findByCompanyId("company1");
        verify(cmpPostedJobsRepository).save(mockJobModel);
    }

    @Test
    void updatePostedJob_jobNotFound_returnsNull() {
        when(cmpPostedJobsRepository.findByCompanyId("company1")).thenReturn(List.of(mockJobModel));

        PostedJobsDTO result = cmpPostedJobsService.updatePostedJob("company1", "job2", mockJobDTO);

        assertNull(result);
        verify(cmpPostedJobsRepository).findByCompanyId("company1");
        verify(cmpPostedJobsRepository, never()).save(any());
    }

    @Test
    void deletePostedJob_removesJob() {
        when(cmpPostedJobsRepository.findByCompanyId("company1")).thenReturn(List.of(mockJobModel));
        when(cmpPostedJobsRepository.save(mockJobModel)).thenReturn(mockJobModel);

        cmpPostedJobsService.deletePostedJob("company1", "job1");

        assertTrue(mockJobModel.getPostedJobs().isEmpty());
        verify(cmpPostedJobsRepository).findByCompanyId("company1");
        verify(cmpPostedJobsRepository).save(mockJobModel);
    }

    @Test
    void deletePostedJob_noJobsForCompany_doesNothing() {
        when(cmpPostedJobsRepository.findByCompanyId("company1")).thenReturn(Collections.emptyList());

        cmpPostedJobsService.deletePostedJob("company1", "job1");

        verify(cmpPostedJobsRepository).findByCompanyId("company1");
        verify(cmpPostedJobsRepository, never()).save(any());
    }

    @Test
    void getCmpPostedJobsByCompanyIdAsync_returnsJobsList() throws Exception {
        when(cmpPostedJobsRepository.findByCompanyId("company1")).thenReturn(List.of(mockJobModel));

        CompletableFuture<List<CmpPostedJobsModel>> result = cmpPostedJobsService.getCmpPostedJobsByCompanyIdAsync("company1");

        assertNotNull(result);
        assertEquals(1, result.get().size());
        assertEquals("jobModel1", result.get().get(0).getId());
        verify(cmpPostedJobsRepository).findByCompanyId("company1");
    }

    @Test
    void findAndUpdateCompanyLevel_updatesLevel() {
        mockJobModel.setCompanyLevel("Basic");
        when(cmpPostedJobsRepository.findByCompanyId("company1")).thenReturn(List.of(mockJobModel));
        when(cmpPostedJobsRepository.save(mockJobModel)).thenReturn(mockJobModel);

        cmpPostedJobsService.findAndUpdateCompanyLevel("company1", "Premium");

        assertEquals("Premium", mockJobModel.getCompanyLevel());
        verify(cmpPostedJobsRepository).findByCompanyId("company1");
        verify(cmpPostedJobsRepository).save(mockJobModel);
    }

    @Test
    void findAndUpdateCompanyLevel_noJobs_doesNothing() {
        when(cmpPostedJobsRepository.findByCompanyId("company1")).thenReturn(Collections.emptyList());

        cmpPostedJobsService.findAndUpdateCompanyLevel("company1", "Premium");

        verify(cmpPostedJobsRepository).findByCompanyId("company1");
        verify(cmpPostedJobsRepository, never()).save(any());
    }
}
