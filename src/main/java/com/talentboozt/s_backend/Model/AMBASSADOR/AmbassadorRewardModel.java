package com.talentboozt.s_backend.Model.AMBASSADOR;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter

@Document(collection = "ambassador_rewards")
public class AmbassadorRewardModel {
    @Id
    private String id;
    private String ambassadorId;
    private String rewardType; // "SWAG", "VOUCHER", "DISCOUNT"
    private String status; // "PENDING", "SHIPPED", "REDEEMED"
    private Instant issuedAt;
    private Instant redeemedAt;
}
