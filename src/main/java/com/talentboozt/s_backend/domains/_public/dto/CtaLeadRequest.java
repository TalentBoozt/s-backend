package com.talentboozt.s_backend.domains._public.dto;

public class CtaLeadRequest {
    private String name;
    private String email;
    private String serviceType;
    private String ctaType;
    private String focusArea;
    private String message;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public String getCtaType() { return ctaType; }
    public void setCtaType(String ctaType) { this.ctaType = ctaType; }

    public String getFocusArea() { return focusArea; }
    public void setFocusArea(String focusArea) { this.focusArea = focusArea; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
