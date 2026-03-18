package com.talentboozt.s_backend.domains.edu.model;

import com.talentboozt.s_backend.domains.edu.enums.EGradingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_quiz_attempts")
@CompoundIndexes({
    @CompoundIndex(name = "user_quiz_idx", def = "{'userId': 1, 'quizId': 1}")
})
public class EQuizAttempts {
    @Id
    private String id;
    
    @Indexed
    private String userId;
    
    @Indexed
    private String quizId;
    
    // Map of questionId -> selected answer(s)
    private Map<String, String[]> userAnswers;
    
    private Double score;
    private Double percentage;
    
    private EGradingStatus status;
    private Boolean isLatest;
    
    private Instant startedAt;
    private Instant completedAt;
    
    @CreatedDate
    private Instant createdAt;
}
