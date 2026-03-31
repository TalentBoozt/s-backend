package com.talentboozt.s_backend.domains.edu.model;

import java.time.Instant;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_coupon_redemptions")
@CompoundIndexes({
    @CompoundIndex(name = "coupon_user_unique_idx", def = "{'couponId': 1, 'userId': 1}", unique = true)
})
public class ECouponRedemption {
    @Id
    private String id;
    
    @Indexed
    private String couponId;
    
    @Indexed
    private String userId;
    
    private String transactionId;
    
    @CreatedDate
    private Instant redeemedAt;
}
