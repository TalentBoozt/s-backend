package com.talentboozt.s_backend.domains.lifeplanner.user.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "lp_preferences")
public class UserPreferences {
    @Id
    private String id;
    private String userId;
    private boolean notificationsEnabled;
    private String theme;
}
