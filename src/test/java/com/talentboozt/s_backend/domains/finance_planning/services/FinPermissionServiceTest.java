package com.talentboozt.s_backend.domains.finance_planning.services;

import com.talentboozt.s_backend.domains.auth.model.CredentialsModel;
import com.talentboozt.s_backend.domains.auth.repository.mongodb.CredentialsRepository;
import com.talentboozt.s_backend.domains.finance_planning.security.rbac.FinPermission;
import com.talentboozt.s_backend.domains.finance_planning.security.rbac.OrganizationRole;
import com.talentboozt.s_backend.domains.finance_planning.security.rbac.PlatformRole;
import com.talentboozt.s_backend.shared.security.model.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FinPermissionServiceTest {

    @Mock
    private CredentialsRepository credentialsRepository;

    @InjectMocks
    private FinPermissionService permissionService;

    private CustomUserDetails userDetails;
    private CredentialsModel user;
    private final String userId = "user123";
    private final String orgId = "org123";

    @BeforeEach
    void setUp() {
        userDetails = new CustomUserDetails(userId, "test@test.com", "pass", Collections.emptyList(), null);
        user = new CredentialsModel();
        user.setId(userId);
    }

    @Test
    void platformAdmin_ShouldBypassAllChecks() {
        user.setPlatformRole(PlatformRole.PLATFORM_ADMIN.name());
        when(credentialsRepository.findById(userId)).thenReturn(Optional.of(user));

        assertTrue(permissionService.hasPermission(userDetails, FinPermission.MANAGE_USERS, orgId, null));
    }

    @Test
    void viewer_ShouldOnlyHaveReadAccess() {
        user.setOrganizations(List.of(Map.of("id", orgId, "role", OrganizationRole.FINANCE_VIEWER.name())));
        when(credentialsRepository.findById(userId)).thenReturn(Optional.of(user));

        assertTrue(permissionService.hasPermission(userDetails, FinPermission.READ_PROJECT, orgId, null));
        assertFalse(permissionService.hasPermission(userDetails, FinPermission.EDIT_FINANCIALS, orgId, null));
    }

    @Test
    void manager_ShouldEditButNotManageUsers() {
        user.setOrganizations(List.of(Map.of("id", orgId, "role", OrganizationRole.FINANCE_PROJECT_MANAGER.name())));
        when(credentialsRepository.findById(userId)).thenReturn(Optional.of(user));

        assertTrue(permissionService.hasPermission(userDetails, FinPermission.EDIT_FINANCIALS, orgId, null));
        assertTrue(permissionService.hasPermission(userDetails, FinPermission.MANAGE_SCENARIOS, orgId, null));
        assertFalse(permissionService.hasPermission(userDetails, FinPermission.MANAGE_USERS, orgId, null));
    }

    @Test
    void staff_ShouldHaveScopedAccess() {
        user.setPlatformRole(PlatformRole.STAFF.name());
        when(credentialsRepository.findById(userId)).thenReturn(Optional.of(user));

        assertTrue(permissionService.hasPermission(userDetails, FinPermission.READ_PROJECT, orgId, null));
        assertFalse(permissionService.hasPermission(userDetails, FinPermission.MANAGE_USERS, orgId, null));
    }
}
