package com.talentboozt.s_backend.domains.auth.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter

@Document(collection = "roles")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleModel {
    @Id
    private String id;
    @Indexed(unique = true)
    private String name;
    private List<String> permissions;
    private String description;
}

