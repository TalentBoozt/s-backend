package com.talentboozt.s_backend.domains.com_job_portal.controller;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.talentboozt.s_backend.domains.com_job_portal.dto.PostedJobsDTO;
import com.talentboozt.s_backend.domains.com_job_portal.model.CmpPostedJobsModel;
import com.talentboozt.s_backend.domains.com_job_portal.service.CmpPostedJobsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class CmpPostedJobsControllerTest {

    @Mock
    private CmpPostedJobsService cmpPostedJobsService;

    @InjectMocks
    private CmpPostedJobsController cmpPostedJobsController;

    private CmpPostedJobsModel mockJob;
    private PostedJobsDTO mockJobDTO;

    @BeforeEach
    void setUp() {
        mockJob = new CmpPostedJobsModel();
        mockJob.setId("job1");
        mockJob.setCompanyId("company1");

        mockJobDTO = new PostedJobsDTO();
        mockJobDTO.setId("job1");
    }

    @Test
    void getCmpPostedJobsByCompanyId_returnsJobsList() {
        when(cmpPostedJobsService.getCmpPostedJobsByCompanyId("company1")).thenReturn(List.of(mockJob));

        List<CmpPostedJobsModel> result = cmpPostedJobsController.getCmpPostedJobsByCompanyId("company1");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("job1", result.get(0).getId());
        verify(cmpPostedJobsService).getCmpPostedJobsByCompanyId("company1");
    }

    @Test
    void getAllCmpPostedJobs_returnsAllJobs() {
        when(cmpPostedJobsService.getAllCmpPostedJobs()).thenReturn(List.of(mockJob));

        List<CmpPostedJobsModel> result = cmpPostedJobsController.getAllCmpPostedJobs();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("job1", result.get(0).getId());
        verify(cmpPostedJobsService).getAllCmpPostedJobs();
    }

    @Test
    void addCmpPostedJobs_addsAndReturnsJob() {
        when(cmpPostedJobsService.addCmpPostedJobs(mockJob)).thenReturn(mockJob);

        CmpPostedJobsModel result = cmpPostedJobsController.addCmpPostedJobs(mockJob);

        assertNotNull(result);
        assertEquals("job1", result.getId());
        verify(cmpPostedJobsService).addCmpPostedJobs(mockJob);
    }

    @Test
    void getPostedJobByJobId_returnsJobDTO() {
        when(cmpPostedJobsService.getPostedJobByJobId("company1", "job1")).thenReturn(mockJobDTO);

        PostedJobsDTO result = cmpPostedJobsController.getPostedJobByJobId("company1", "job1");

        assertNotNull(result);
        assertEquals("job1", result.getId());
        verify(cmpPostedJobsService).getPostedJobByJobId("company1", "job1");
    }

    @Test
    void updatePostedJob_updatesAndReturnsJobDTO() {
        when(cmpPostedJobsService.updatePostedJob("company1", "job1", mockJobDTO)).thenReturn(mockJobDTO);

        PostedJobsDTO result = cmpPostedJobsController.updatePostedJob("company1", "job1", mockJobDTO);

        assertNotNull(result);
        assertEquals("job1", result.getId());
        verify(cmpPostedJobsService).updatePostedJob("company1", "job1", mockJobDTO);
    }

    @Test
    void deletePostedJob_deletesJob() {
        cmpPostedJobsController.deletePostedJob("company1", "job1");

        verify(cmpPostedJobsService).deletePostedJob("company1", "job1");
    }
}
