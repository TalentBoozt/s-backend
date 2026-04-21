package com.talentboozt.s_backend.domains.edu.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_api_keys")
public class EApiKey {
    @Id
    private String id;

    @Indexed(unique = true)
    private String apiKey; // Hashed version for storage

    private String apiKeyHint; // e.g., "sk_live_...1234"

    @Indexed
    private String ownerId; // UserId

    private String name; // Label for the key

    private boolean isActive;

    private List<String> allowedIps; // Optional IP whitelisting
    private List<String> scopes; // e.g., ["READ_COURSES", "WRITE_ANALYTICS"]

    private Instant expiresAt;
    private Instant lastUsedAt;
    private Instant createdAt;
}
