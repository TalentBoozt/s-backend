package com.talentboozt.s_backend.domains.portal.resume.controller;

import com.talentboozt.s_backend.shared.identity.model.CredentialsModel;
import com.talentboozt.s_backend.shared.security.service.JwtService;
import com.talentboozt.s_backend.shared.utils.JwtUtil;
import com.talentboozt.s_backend.shared.auth.service.CustomUserDetailsService;
import com.talentboozt.s_backend.shared.ai.tool.service.AIUsageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.nio.file.Files;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

@SpringBootTest
@AutoConfigureMockMvc
public class AtsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private AIUsageService aiUsageService;

    @BeforeEach
    void setupAuth() {
        // Setup mock authentication for filter
        when(jwtService.extractTokenFromHeaderOrCookie(any())).thenReturn("mock-token");
        when(jwtService.validateToken("mock-token")).thenReturn(true);
        
        when(jwtUtil.extractUsername(anyString())).thenReturn("mock-user");
        when(jwtUtil.validateToken(anyString(), anyString())).thenReturn(true);
        
        UserDetails mockUserDetails = new User("mock-user", "password", Collections.emptyList());
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(mockUserDetails);

        // Setup mock authentication for Controller argument resolver
        CredentialsModel mockUser = new CredentialsModel();
        mockUser.setEmployeeId("mock-user-id");
        when(jwtService.getUserFromToken("mock-token")).thenReturn(mockUser);

        // Setup mock AIUsageService to do nothing on quota check/consume
        doNothing().when(aiUsageService).consumeCredits(anyString(), any(), any());
        doNothing().when(aiUsageService).checkQuota(anyString(), any(), any());
    }

    @Test
    void testAnalyzeFailingResume() throws Exception {
        File pdfFile = new File("../scratch/actual_failing_resume.pdf");
        if (!pdfFile.exists()) {
            System.out.println("Failing PDF file not found!");
            return;
        }

        byte[] content = Files.readAllBytes(pdfFile.toPath());
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "actual_failing_resume.pdf",
                "application/pdf",
                content
        );

        System.out.println("Sending actual_failing_resume.pdf to ATS analyzer endpoint...");
        var result = mockMvc.perform(multipart("/api/v2/ats/analyze")
                        .file(multipartFile)
                        .param("jobDescription", "Software engineer and PDF expert developer")
                        .header("Authorization", "Bearer mock-token")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andReturn();

        System.out.println("Response Status: " + result.getResponse().getStatus());
        System.out.println("Response Content:\n" + result.getResponse().getContentAsString());
    }

    @Test
    void testAnalyzeWorkingResume() throws Exception {
        File pdfFile = new File("../scratch/working_resume.pdf");
        if (!pdfFile.exists()) {
            System.out.println("Working PDF file not found!");
            return;
        }

        byte[] content = Files.readAllBytes(pdfFile.toPath());
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "working_resume.pdf",
                "application/pdf",
                content
        );

        System.out.println("Sending working_resume.pdf to ATS analyzer endpoint...");
        var result = mockMvc.perform(multipart("/api/v2/ats/analyze")
                        .file(multipartFile)
                        .param("jobDescription", "Software engineer and PDF expert developer")
                        .header("Authorization", "Bearer mock-token")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andReturn();

        System.out.println("Response Status: " + result.getResponse().getStatus());
        System.out.println("Response Content:\n" + result.getResponse().getContentAsString());
    }
}
