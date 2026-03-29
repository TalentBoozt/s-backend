package com.talentboozt.s_backend.domains.edu.dto.workspace;

import com.talentboozt.s_backend.domains.edu.enums.ERoles;
import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class WorkspaceMemberDTO {
    private String id;
    private String workspaceId;
    private String userId;
    private String userName;
    private String userEmail;
    private String userAvatar;
    private ERoles role;
    private String status;
    private Instant joinedAt;
}
