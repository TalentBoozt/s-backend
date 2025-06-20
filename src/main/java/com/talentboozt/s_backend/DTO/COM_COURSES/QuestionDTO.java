package com.talentboozt.s_backend.DTO.COM_COURSES;

import lombok.Setter;
import lombok.Getter;

import java.util.List;

@Getter
@Setter
public class QuestionDTO {
    private String id;
    private String questionText;
    private String questionType; // "multiple-choice", "text", "paragraph", "true-false", "likert", "fill-blank"
    private List<String> options; // Used for multiple-choice, true-false, likert, etc.
    private List<String> correctAnswer; // List to support multiple correct answers
    private String explanation; // Optional explanation
    private boolean required;
}
