package com.talentboozt.s_backend.DTO.PLAT_COURSES;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuestionAnswer {
    private String questionId;
    private List<String> selectedAnswers;  // Support for multiple answers
    private boolean correct;  // Auto-check for objective types (optional)
    private String feedback;  // Optional: feedback for user or explanation
}
