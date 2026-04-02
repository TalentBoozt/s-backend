package com.talentboozt.s_backend.domains.edu.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_user_preferences")
public class EUserPreferences {
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String userId;
    
    private List<String> interests; // Array of category tags or topics
    private String preferredLanguage; // en, es, fr
    private Integer dailyLearningGoalMinutes; // E.g. 30, 60
    private Boolean isNotificationsEnabled;
    private String preferredDifficulty; // BEGINNER, INTERMEDIATE, ADVANCED
    private String careerGoal; // SWITCH, PROMOTION, etc
    
    private Instant createdAt;
    private Instant updatedAt;
}
