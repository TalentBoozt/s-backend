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
@Document(collection = "user_reputation")
public class UserReputation {
    @Id
    private String id;

    @Indexed(unique = true)
    private String userId;

    private long totalScore;
    private long articleScore;
    private long communityScore;
    private long announcementScore;

    private LocalDateTime lastUpdated;
}
