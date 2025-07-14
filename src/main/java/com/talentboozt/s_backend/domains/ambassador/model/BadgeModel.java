package com.talentboozt.s_backend.domains.ambassador.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter

@Document("ambassador_badges")
@CompoundIndex(name = "unique_badge", def = "{'ambassadorId': 1, 'badgeId': 1}", unique = true)
public class BadgeModel {
    @Id
    private String id;
    private String ambassadorId;
    private String taskId;
    private String badgeId;
    private String title;
    private String description;
    private Instant earnedAt;
}
