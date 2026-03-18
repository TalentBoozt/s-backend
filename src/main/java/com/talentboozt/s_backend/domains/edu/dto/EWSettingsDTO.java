package com.talentboozt.s_backend.domains.edu.dto;

import lombok.Data;

@Data
public class EWSettingsDTO {
    // Only keeping base workspace configs, removed unbounded Member/Block arrays mapping arrays natively to Collections!
    private Boolean allowPublicRegistration;
    private Boolean requireAdminApproval;
    private Boolean isBrandingEnabled;
    private String defaultRole; 
}
