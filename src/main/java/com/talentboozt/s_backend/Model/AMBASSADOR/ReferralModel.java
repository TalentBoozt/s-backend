package com.talentboozt.s_backend.Model.AMBASSADOR;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter

@Document(collection = "ambassador_referrals")
public class ReferralModel {
    @Id
    private String id;

    @Indexed
    private String referralCode;

    @Indexed
    private String ambassadorId; // Link to AmbassadorProfileModel

    private String referredUserId; // Link to EmployeeModel or CredentialsModel

    private Instant referredAt;

    private String referredPlatform; // e.g., "LearningPlatform"
    private boolean courseEnrolled;
    private Instant enrolledAt;
}
