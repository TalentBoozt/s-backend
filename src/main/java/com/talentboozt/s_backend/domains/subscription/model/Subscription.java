package com.talentboozt.s_backend.domains.subscription.model;

import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;
import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "subscriptions")
public class Subscription {
    @Id
    private String id;

    @Indexed(unique = true)
    private String userId;

    @Indexed
    private ESubscriptionPlan plan;

    @Indexed
    private ESubscriptionStatus status;

    private Instant startDate;
    private Instant endDate;
    
    @Indexed(sparse = true)
    private String stripeCustomerId;

    @Indexed(sparse = true)
    private String stripeSubscriptionId;

    private String stripePriceId;
    private String billingCycle;
    
    @Builder.Default
    private Boolean autoRenew = true;

    @Builder.Default
    private Boolean cancelAtPeriodEnd = false;

    private Instant trialEndDate;
    private Instant cancelledAt;
    private Instant lastPaymentAt;
    private Instant lastCreditResetAt;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
