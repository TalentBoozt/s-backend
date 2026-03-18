package com.talentboozt.s_backend.domains.edu.dto.evaluation;

import com.talentboozt.s_backend.domains.edu.enums.EQuestionType;
import lombok.Data;

@Data
public class EQuestionDTO {
    private String id;
    private String text;
    private EQuestionType type;
    private String[] options;
    private String[] correctAnswers;
    private String explanation;
    private Double points;
}
