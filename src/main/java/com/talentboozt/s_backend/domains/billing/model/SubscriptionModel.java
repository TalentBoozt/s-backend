package com.talentboozt.s_backend.domains.billing.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "subscriptions")
public class SubscriptionModel {
    @Id
    private String id;
    private String organizationId;
    private String tier; // FREE, PRO, ENTERPRISE
    private String status; // ACTIVE, CANCELED, PAST_DUE, TRIALING
    
    private String stripeCustomerId;
    private String stripeSubscriptionId;
    
    private Instant currentPeriodStart;
    private Instant currentPeriodEnd;
    
    private Map<String, Integer> usageLimits; // E.g., {"JOB_POSTS": 10, "AI_TOKENS": 5000}
    private Map<String, Integer> currentUsage;
    
    private Instant createdAt;
    private Instant updatedAt;
}
