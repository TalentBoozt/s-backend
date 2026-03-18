package com.talentboozt.s_backend.domains.edu.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.talentboozt.s_backend.domains.edu.enums.ERoles;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_workspace_members")
@CompoundIndexes({
    @CompoundIndex(name = "workspace_user_idx", def = "{'workspaceId': 1, 'userId': 1}", unique = true)
})
public class EWorkspaceMembers {
    @Id
    private String id;
    
    @Indexed
    private String workspaceId;
    
    @Indexed
    private String userId;
    
    @Indexed
    private ERoles role;
    
    private String status;
    private String department;
    
    private String[] assignedCourseIds;
    private String[] completedCourseIds;
    
    private String invitedBy;
    private String createdBy;
    
    @CreatedDate
    private Instant joinedAt;
    
    private Instant lastActiveAt;
}
