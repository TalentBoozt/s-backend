package com.talentboozt.s_backend.domains.plat_job_portal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talentboozt.s_backend.domains.plat_job_portal.model.InterviewQuestionModel;
import com.talentboozt.s_backend.domains.plat_job_portal.service.InterviewQuestionService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class InterviewQuestionControllerTest {

    @Mock
    private InterviewQuestionService service;

    @InjectMocks
    private InterviewQuestionController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private InterviewQuestionModel question;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
        List<InterviewQuestionModel.Question> questions = List.of(new InterviewQuestionModel.Question("1", "What is Java?", null, 0, null));
        question = new InterviewQuestionModel("1", "Java", questions);
    }

    @Test
    void addQuestion_shouldReturnSavedQuestion() throws Exception {
        when(service.saveQuestion(any(InterviewQuestionModel.class))).thenReturn(question);

        mockMvc.perform(post("/api/v2/interview-questions/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(question)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.category").value("Java"));

        verify(service, times(1)).saveQuestion(any(InterviewQuestionModel.class));
    }

    @Test
    void getAllQuestions_shouldReturnQuestionList() throws Exception {
        List<InterviewQuestionModel> questions = Arrays.asList(question);
        when(service.getAllQuestions()).thenReturn(questions);

        mockMvc.perform(get("/api/v2/interview-questions/get")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].category").value("Java"));

        verify(service, times(1)).getAllQuestions();
    }

    @Test
    void incrementQuestionViewCount_shouldReturnSuccessResponse() throws Exception {
        doNothing().when(service).incrementQuestionViewCount(eq("1"));

        mockMvc.perform(put("/api/v2/interview-questions/increment-question-view/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Question view count incremented"));

        verify(service, times(1)).incrementQuestionViewCount("1");
    }

    @Test
    void incrementAnswerViewCount_shouldReturnSuccessResponse() throws Exception {
        doNothing().when(service).incrementAnswerViewCount(eq("1"));

        mockMvc.perform(put("/api/v2/interview-questions/increment-answer-view/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Answer view count incremented"));

        verify(service, times(1)).incrementAnswerViewCount("1");
    }
}
