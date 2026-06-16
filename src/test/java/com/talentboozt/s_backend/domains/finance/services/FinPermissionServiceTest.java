package com.talentboozt.s_backend.domains.finance.services;

import com.talentboozt.s_backend.domains.finance.security.rbac.FinPermission;
import com.talentboozt.s_backend.domains.finance.security.rbac.ProjectRole;
import com.talentboozt.s_backend.domains.finance.models.FinProject;
import com.talentboozt.s_backend.domains.finance.models.FinProjectMember;
import com.talentboozt.s_backend.domains.finance.repository.mongodb.FinProjectMemberRepository;
import com.talentboozt.s_backend.domains.finance.repository.mongodb.FinProjectRepository;
import com.talentboozt.s_backend.shared.security.model.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FinPermissionServiceTest {

    @Mock
    private FinProjectRepository projectRepository;

    @Mock
    private FinProjectMemberRepository memberRepository;

    @InjectMocks
    private FinPermissionService permissionService;

    private CustomUserDetails userDetails;
    private final String userId = "user123";
    private final String orgId = "org123";
    private final String projectId = "proj123";

    @BeforeEach
    void setUp() {
        userDetails = new CustomUserDetails(userId, "test@test.com", "pass", Collections.emptyList(), null);
    }

    @Test
    void platformAdmin_ShouldBypassAllChecks() {
        CustomUserDetails adminDetails = new CustomUserDetails(userId, "admin@test.com", "pass", 
                List.of(new SimpleGrantedAuthority("ROLE_PLATFORM_ADMIN")), null);

        assertTrue(permissionService.hasPermission(adminDetails, FinPermission.MANAGE_USERS, orgId, null));
    }

    @Test
    void viewer_ShouldOnlyHaveReadAccess() {
        // Test global read project permission
        assertTrue(permissionService.hasPermission(userDetails, FinPermission.READ_PROJECT, orgId, null));
        // Test global write permission without project context (not admin)
        assertFalse(permissionService.hasPermission(userDetails, FinPermission.EDIT_FINANCIALS, orgId, null));
    }

    @Test
    void projectEditor_ShouldEditButNotManageUsers() {
        FinProject project = new FinProject();
        project.setId(projectId);
        project.setOwnerId("anotherUser");
        project.setStatus("ACTIVE");

        FinProjectMember member = new FinProjectMember();
        member.setUserId(userId);
        member.setProjectId(projectId);
        member.setRole(ProjectRole.PROJECT_EDITOR);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(memberRepository.findByProjectIdAndUserId(projectId, userId)).thenReturn(Optional.of(member));

        assertTrue(permissionService.hasPermission(userDetails, FinPermission.EDIT_FINANCIALS, orgId, projectId));
        assertTrue(permissionService.hasPermission(userDetails, FinPermission.MANAGE_SCENARIOS, orgId, projectId));
        assertFalse(permissionService.hasPermission(userDetails, FinPermission.MANAGE_USERS, orgId, projectId));
    }

    @Test
    void projectOwner_ShouldHaveAllPermissions() {
        FinProject project = new FinProject();
        project.setId(projectId);
        project.setOwnerId(userId);
        project.setStatus("ACTIVE");

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        assertTrue(permissionService.hasPermission(userDetails, FinPermission.MANAGE_USERS, orgId, projectId));
    }
}
