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
@Document(collection = "referral_codes")
public class ReferralCode {
    @Id
    private String id;

    @Indexed(unique = true)
    private String code;

    @Indexed
    private String userId;

    private Instant createdAt;
}
