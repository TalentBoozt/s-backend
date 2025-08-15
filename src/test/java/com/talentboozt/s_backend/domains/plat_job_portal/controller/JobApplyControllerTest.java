package com.talentboozt.s_backend.domains.plat_job_portal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talentboozt.s_backend.domains.plat_job_portal.dto.JobApplicantDTO;
import com.talentboozt.s_backend.domains.plat_job_portal.dto.JobViewerDTO;
import com.talentboozt.s_backend.domains.plat_job_portal.model.JobApplyModel;
import com.talentboozt.s_backend.domains.plat_job_portal.service.JobApplyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class JobApplyControllerTest {

    @Mock
    private JobApplyService jobApplyService;

    @InjectMocks
    private JobApplyController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private JobApplyModel jobApply;
    private JobApplicantDTO jobApplicantDTO;
    private JobViewerDTO jobViewerDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
        jobApply = new JobApplyModel("1", "comp1", "job1", null, null);
        jobApplicantDTO = new JobApplicantDTO("id", "applicant1", "John Doe", null, null, null, null, null, null, null);
        jobViewerDTO = new JobViewerDTO("viewer1", "applicant1", "Jane Doe", null, null);
    }

    @Test
    void getAllJobApply_shouldReturnJobApplyList() throws Exception {
        List<JobApplyModel> jobApplies = Arrays.asList(jobApply);
        when(jobApplyService.getAllJobApply()).thenReturn(jobApplies);

        mockMvc.perform(get("/api/v2/cmp_job-apply/getAll")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"));

        verify(jobApplyService, times(1)).getAllJobApply();
    }

    @Test
    void getJobApplyByCompanyId_shouldReturnJobApplyList() throws Exception {
        List<JobApplyModel> jobApplies = Arrays.asList(jobApply);
        when(jobApplyService.getJobApplyByCompanyId("comp1")).thenReturn(Optional.of(jobApplies));

        mockMvc.perform(get("/api/v2/cmp_job-apply/getByCompanyId/comp1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"));

        verify(jobApplyService, times(1)).getJobApplyByCompanyId("comp1");
    }

    @Test
    void addJobApply_shouldReturnSavedJobApply() throws Exception {
        when(jobApplyService.addJobApply(any(JobApplyModel.class))).thenReturn(jobApply);

        mockMvc.perform(post("/api/v2/cmp_job-apply/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobApply)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));

        verify(jobApplyService, times(1)).addJobApply(any(JobApplyModel.class));
    }

    @Test
    void addJobApplicant_shouldReturnSavedJobApply() throws Exception {
        when(jobApplyService.addJobApplicant(eq("comp1"), eq("job1"), any(JobApplicantDTO.class))).thenReturn(jobApply);

        mockMvc.perform(post("/api/v2/cmp_job-apply/addApplicant/comp1/job1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobApplicantDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));

        verify(jobApplyService, times(1)).addJobApplicant(eq("comp1"), eq("job1"), any(JobApplicantDTO.class));
    }

    @Test
    void addJobViewer_shouldReturnSavedJobApply() throws Exception {
        when(jobApplyService.addJobViewer(eq("comp1"), eq("job1"), any(JobViewerDTO.class))).thenReturn(jobApply);

        mockMvc.perform(post("/api/v2/cmp_job-apply/addViewer/comp1/job1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobViewerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));

        verify(jobApplyService, times(1)).addJobViewer(eq("comp1"), eq("job1"), any(JobViewerDTO.class));
    }

    @Test
    void getSingleJobApplyByJobId_shouldReturnJobApplicantDTO() throws Exception {
        when(jobApplyService.getSingleJobApplyByJobId("comp1", "applicant1")).thenReturn(jobApplicantDTO);

        mockMvc.perform(get("/api/v2/cmp_job-apply/getSingleByCompanyId/comp1/jobApply/applicant1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value("applicant1"));

        verify(jobApplyService, times(1)).getSingleJobApplyByJobId("comp1", "applicant1");
    }

    @Test
    void updateSingleJobApply_shouldReturnUpdatedJobApplicantDTO() throws Exception {
        when(jobApplyService.updateSingleJobApply(eq("comp1"), eq("applicant1"), any(JobApplicantDTO.class)))
                .thenReturn(jobApplicantDTO);

        mockMvc.perform(put("/api/v2/cmp_job-apply/updateByCompanyId/comp1/jobApply/applicant1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobApplicantDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value("applicant1"));

        verify(jobApplyService, times(1)).updateSingleJobApply(eq("comp1"), eq("applicant1"), any(JobApplicantDTO.class));
    }

    @Test
    void deleteJobApply_shouldCallServiceDelete() throws Exception {
        doNothing().when(jobApplyService).deleteJobApply("1");

        mockMvc.perform(delete("/api/v2/cmp_job-apply/delete/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(jobApplyService, times(1)).deleteJobApply("1");
    }
}
