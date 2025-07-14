package com.talentboozt.s_backend.domains.plat_job_portal.controller;

import com.talentboozt.s_backend.domains.common.dto.ApiResponse;
import com.talentboozt.s_backend.domains.plat_job_portal.model.InterviewQuestionModel;
import com.talentboozt.s_backend.domains.plat_job_portal.service.InterviewQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/interview-questions")
public class InterviewQuestionController {

    private final InterviewQuestionService service;

    @Autowired
    public InterviewQuestionController(InterviewQuestionService service) {
        this.service = service;
    }

    @PostMapping("/add")
    public InterviewQuestionModel addQuestion(@RequestBody InterviewQuestionModel question) {
        return service.saveQuestion(question);
    }

    @GetMapping("/get")
    public List<InterviewQuestionModel> getAllQuestions() {
        return service.getAllQuestions();
    }

    @PutMapping("/increment-question-view/{id}")
    public ResponseEntity<ApiResponse> incrementQuestionViewCount(@PathVariable String id) {
        service.incrementQuestionViewCount(id);
        return ResponseEntity.ok(new ApiResponse("Question view count incremented"));
    }

    @PutMapping("/increment-answer-view/{id}")
    public ResponseEntity<ApiResponse> incrementAnswerViewCount(@PathVariable String id) {
        service.incrementAnswerViewCount(id);
        return ResponseEntity.ok(new ApiResponse("Answer view count incremented"));
    }
}
