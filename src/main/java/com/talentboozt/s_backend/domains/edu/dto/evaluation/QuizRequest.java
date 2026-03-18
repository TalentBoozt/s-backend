package com.talentboozt.s_backend.domains.edu.dto.evaluation;

import com.talentboozt.s_backend.domains.edu.enums.EQuizType;
import lombok.Data;
import java.util.List;

@Data
public class QuizRequest {
    private String title;
    private String description;
    private EQuizType type;
    private Integer durationLimit;
    private Double passingScore;
    private List<EQuestionDTO> questions;
    private Boolean isPublished;
    private Integer allowRetakes;
}
