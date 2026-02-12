package com.talentboozt.s_backend.domains.reputation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reputation_events")
public class ReputationEvent {
    @Id
    private String id;

    @Indexed
    private String userId;

    private ReputationSourceType sourceType;
    private String sourceId;
    private int delta;

    private LocalDateTime createdAt;
}
