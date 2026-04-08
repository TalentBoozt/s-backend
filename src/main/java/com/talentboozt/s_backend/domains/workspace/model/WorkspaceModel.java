package com.talentboozt.s_backend.domains.workspace.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Document(collection = "workspaces")
public class WorkspaceModel {
    @Id
    private String id;
    private String name;
    private String slug;
    private String ownerId;
    private List<String> memberIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String subscriptionType; // FREE, PRO, ENTERPRISE
}
