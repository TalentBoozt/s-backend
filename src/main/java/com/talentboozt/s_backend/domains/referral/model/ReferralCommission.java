package com.talentboozt.s_backend.domains.referral.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "referral_commissions")
public class ReferralCommission {
    @Id
    private String id;

    @Indexed
    private String referrerId;

    @Indexed
    private String referredCreatorId;

    private double percentage; // default 5%
    private Instant expiryDate;
    private Instant createdAt;
}
