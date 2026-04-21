package com.talentboozt.s_backend.domains.edu.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_system_settings")
public class ESystemSettings {
    @Id
    private String id;

    @Indexed(unique = true)
    private String category; // e.g., "GENERAL", "SECURITY", "EMAIL", "MAINTENANCE"

    private Map<String, Object> settings;

    private Instant updatedAt;
    private String updatedBy;
}
