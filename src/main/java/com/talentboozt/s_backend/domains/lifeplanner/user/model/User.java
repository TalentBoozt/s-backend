package com.talentboozt.s_backend.domains.lifeplanner.user.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data
@Document(collection = "lp_users")
public class User {
    @Id
    private String id;
    private String email;
    private String passwordHash;
    private String name;
    private Instant createdAt;
    private Instant updatedAt;
}
