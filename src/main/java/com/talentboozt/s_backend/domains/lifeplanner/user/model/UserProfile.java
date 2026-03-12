package com.talentboozt.s_backend.domains.lifeplanner.user.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import java.time.Instant;

@Data
@Document(collection = "lp_profiles")
public class UserProfile {
    @Id
    private String id;
    @Indexed
    private String userId;
    private String lifestyleData;
    private List<String> studyPreferences;
    private String focusTime;
    private List<String> hobbies;
    private String stressLevel;
    private Instant createdAt;
    private Instant updatedAt;
}
