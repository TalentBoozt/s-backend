package com.talentboozt.s_backend.domains.community.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "community_activities")
public class Activity {
    @Id
    private String id;
    private String userId;
    private String action; // e.g., "CREATED_POST", "REACTED_TO_COMMENT"
    private String targetId;
    private LocalDateTime timestamp;
}
