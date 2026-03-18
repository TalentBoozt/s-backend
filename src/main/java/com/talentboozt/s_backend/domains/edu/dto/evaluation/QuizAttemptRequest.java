package com.talentboozt.s_backend.domains.edu.dto.evaluation;

import lombok.Data;
import java.util.Map;

@Data
public class QuizAttemptRequest {
    private Map<String, String[]> userAnswers;
}
