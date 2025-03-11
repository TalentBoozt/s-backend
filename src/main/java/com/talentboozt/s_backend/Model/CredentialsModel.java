package com.talentboozt.s_backend.Model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@ToString

@Document(collection = "portal_credentials")
public class CredentialsModel {
    @Id
    private String id;
    private String employeeId;
    private String companyId;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String role;
    private String userLevel; // Free, Pro
    private String registeredFrom; // JobPortal, ResumeBuilder, TrainingPlatform
    private String promotion; // facebook, linkedin
    private String referrerId; // Stores the referrer (e.g., partner company ID or promo source)
    private List<String> accessedPlatforms; // Tracks where the user has logged in
    private boolean active; // Flag to indicate if the user is active
    private boolean disabled; // Flag to indicate if the user is disabled(banned)
}
