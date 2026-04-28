package com.talentboozt.s_backend.domains.referral.model;

import com.talentboozt.s_backend.domains.referral.enums.ReferralStatus;
import com.talentboozt.s_backend.domains.referral.enums.ReferralType;
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
@Document(collection = "referrals")
public class Referral {
    @Id
    private String id;

    @Indexed
    private String referrerId;

    @Indexed
    private String referredUserId;

    private ReferralType type;
    private ReferralStatus status;
    private boolean rewardIssued;
    private Instant createdAt;
    private Instant completedAt;
}
