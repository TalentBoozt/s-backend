package com.talentboozt.s_backend.Model.AMBASSADOR;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter

@Document("ambassador_swag")
@CompoundIndex(name = "unique_swag", def = "{'ambassadorId': 1, 'taskId': 1}", unique = true)
public class SwagModel {
    @Id
    private String id;
    private String ambassadorId;
    private String taskId;
    private String swagType;
    private String status; // PENDING, APPROVED, SHIPPED, DELIVERED
    private Instant requestedAt;
    private Instant shippedAt;
    private Instant deliveredAt;
    private String shippingAddress;
    private String trackingCode;
}
