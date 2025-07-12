package com.talentboozt.s_backend.domains.ambassador.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter

@Document(collection = "ambassador_rewards")
@CompoundIndexes({
        @CompoundIndex(name = "unique_reward", def = "{'ambassadorId': 1, 'taskId': 1}", unique = true)
})
public class AmbassadorRewardModel {
    @Id
    private String id;
    private String ambassadorId;
    private String taskId;
    private String rewardType; // "SWAG", "VOUCHER", "DISCOUNT"
    private String status; // "PENDING", "SHIPPED", "REDEEMED"
    private Instant issuedAt;
    private Instant redeemedAt;
}
