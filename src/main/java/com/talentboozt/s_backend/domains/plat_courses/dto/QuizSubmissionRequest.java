package com.talentboozt.s_backend.domains.plat_courses.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuizSubmissionRequest {
    private String employeeId;
    private String courseId;
    private String moduleId;
    private String quizId;
    private List<QuestionAnswer> answers;
}
