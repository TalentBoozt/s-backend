package com.talentboozt.s_backend.domains.software_marketplace.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@ToString
@Document(collection = "portal_software_apps")
public class SoftwareAppModel {
    @Id
    private String id;
    private String name;
    private String description;
    private String icon; // URL or name for icon
    private String category; // e.g., productivity, analytics, communication
    private String developer;
    private String version;
    private String status; // ACTIVE, EXPIRED, TRIAL, PENDING
    private Instant purchaseDate;
    private Instant expiryDate;
    private String companyId; // Owner of the subscription
    private String planName; // Standard, Premium, Enterprise
    private Double cost;
    private String billingCycle; // Monthly, Yearly, One-time
    private List<String> permissions; // Roles/Permissions required to access
    private String accessUrl; // Link to open the app
    private boolean isGlobal; // If it's a system-wide app available to all
}
