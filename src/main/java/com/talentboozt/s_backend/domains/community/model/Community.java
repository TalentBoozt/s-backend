package com.talentboozt.s_backend.domains.community.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "communities")
public class Community {
    @Id
    private String id;
    private String name;
    private String description;
    private String icon;
    private long memberCount;
    private boolean isJoined;
}
