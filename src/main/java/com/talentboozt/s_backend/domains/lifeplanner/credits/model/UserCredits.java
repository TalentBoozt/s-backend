package com.talentboozt.s_backend.domains.lifeplanner.credits.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "lp_user_credits")
public class UserCredits {
    @Id
    private String id;

    @Indexed(unique = true)
    private String userId;

    private SubscriptionTier tier;
    private int creditsAvailable;
    private Instant lastRefreshedAt;
}
