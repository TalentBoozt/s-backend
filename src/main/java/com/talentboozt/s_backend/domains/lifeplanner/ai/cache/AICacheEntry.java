package com.talentboozt.s_backend.domains.lifeplanner.ai.cache;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data
@Document(collection = "lp_ai_cache")
public class AICacheEntry {
    @Id
    private String id;
    @Indexed(unique = true)
    private String cacheKey;
    private String responseJson;
    private String provider;
    private String promptHash;
    @Indexed(expireAfter = "24h")
    private Instant createdAt;
}
