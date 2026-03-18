package com.talentboozt.s_backend.domains.edu.dto;

import com.talentboozt.s_backend.domains.edu.enums.EThemes;

import lombok.Data;

@Data
public class EPrivacySettingsDTO {
    private boolean profileVisibility = true;
    private boolean contactVisibility = true;
    private boolean experienceVisibility = true;
    private boolean educationVisibility = true;
    private boolean skillsVisibility = true;
    private boolean languagesVisibility = true;
    private boolean interestsVisibility = true;
    private EThemes theme = EThemes.SYSTEM;
    private String defaultLanguage = "en";
    private String timezone = "UTC";
    private String currency = "USD";
    private String country = "US";
    private String state = "CA";
    private String city;
    private String zipCode;
    private String address;
}
