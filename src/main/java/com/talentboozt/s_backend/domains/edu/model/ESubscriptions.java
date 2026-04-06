package com.talentboozt.s_backend.domains.edu.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;
import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_subscriptions")
public class ESubscriptions {
    @Id
    private String id;
    
    @Indexed
    private String userId;
    
    @Indexed
    private ESubscriptionPlan plan;
    
    @Indexed
    private ESubscriptionStatus status;
    
    private Integer remainingCredits;
    private Integer totalCredits;
    
    private Double price;
    @Builder.Default
    private String currency = "USD";
    private String billingCycle;
    @Builder.Default
    private Boolean autoRenew = true;
    private String paymentGatewayId;
    private String stripeCustomerId;
    private String stripeSubscriptionId;
    private String stripePriceId;
    private Double commissionRate;
    private Integer maxCourses;
    private String[] features;
    
    private Instant startDate;
    private Instant endDate;
    private Instant trialEndDate;
    private Instant cancelledAt;
    private Instant lastPaymentAt;

    /** Whether Stripe will cancel at end of current period (graceful cancel). */
    @Builder.Default
    private Boolean cancelAtPeriodEnd = false;

    /** Tracks when credits were last reset to prevent duplicate grants on mid-cycle events. */
    private Instant lastCreditResetAt;
    
    @CreatedDate
    private Instant createdAt;
    
    @LastModifiedDate
    private Instant updatedAt;
}
