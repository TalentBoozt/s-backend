package com.talentboozt.s_backend.domains.finance_planning.models;

import com.talentboozt.s_backend.domains.finance_planning.security.rbac.ProjectRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "fin_project_members")
public class FinProjectMember {
    @Id
    private String id;
    private String projectId;
    private String userId;
    private ProjectRole role;
    private Instant joinedAt;
}
