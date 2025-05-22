package com.talentboozt.s_backend.Model.common.auth;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter

@Document(collection = "permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionModel {
    @Id
    private String id; // e.g., "READ_USERS"
    private String name; // e.g., "Read Users"
    private String description;
    private String category;
    private Instant createdAt;
    private Instant updatedAt;
}
