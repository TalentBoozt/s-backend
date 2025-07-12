package com.talentboozt.s_backend.domains.plat_courses.model;

import com.talentboozt.s_backend.domains.plat_courses.dto.QuestionAnswer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter

@Document(collection = "quiz_attempts")
public class QuizAttempt {
    @Id
    private String id;
    private String employeeId;
    private String courseId;
    private String moduleId;
    private String quizId;
    private int attemptNumber;
    private List<QuestionAnswer> answers;
    private double score;        // Percentage score, e.g. 85.0
    private int correctCount;
    private int totalQuestions;
    private String submittedAt;  // ISO timestamp
}
