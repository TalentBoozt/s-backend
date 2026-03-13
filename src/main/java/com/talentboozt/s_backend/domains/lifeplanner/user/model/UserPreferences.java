package com.talentboozt.s_backend.domains.lifeplanner.user.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Map;

@Data
@Document(collection = "lp_preferences")
public class UserPreferences {
    @Id
    private String id;
    private String userId;
    private boolean notificationsEnabled;
    private String theme;
    
    // Aesthetic Personalization
    private String colorPalette;
    private String fontFamily;
    private String uiDensity;
    private String journalingStyle;
    private String plannerLayoutStyle;
    
    // Productivity & Scheduling
    private String workHoursStart;
    private String workHoursEnd;
    private String preferredStudyTime;
    private String productivityCycle;
    private int breakFrequency;
    private int studySessionLength;
    
    // Professional & Psychological Context
    private String careerType;
    private Map<String, Object> personalityProfile;
}
