package com.talentboozt.s_backend.Model.common.auth;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter

@Document(collection = "permissions")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PermissionModel {
    @Id
    private String id;
    private String name; // e.g., "CAN_CREATE_COURSES"
    private String description;
}
