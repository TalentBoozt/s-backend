package com.talentboozt.s_backend.domains.auth.service;

import com.talentboozt.s_backend.domains.auth.constant.PermissionConstants;
import com.talentboozt.s_backend.domains.auth.model.RoleModel;
import com.talentboozt.s_backend.domains.auth.repository.mongodb.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleSeedService {

    private final RoleRepository roleRepository;

    @PostConstruct
    public void seedRoles() {
        if (roleRepository.count() > 0) {
            log.info("Roles already seeded. Skipping...");
            return;
        }

        log.info("Seeding base roles...");

        // 1. MEMBER (Base Role)
        createRole(PermissionConstants.ROLE_MEMBER, "Standard platform user",
                Arrays.asList(
                        PermissionConstants.MEMBER_READ,
                        PermissionConstants.MEMBER_POST,
                        PermissionConstants.MEMBER_COMMENT,
                        PermissionConstants.CONTENT_CREATE,
                        PermissionConstants.TRAINING_ATTEND),
                null);

        // 2. AMBASSADOR (Inherits from MEMBER)
        createRole(PermissionConstants.ROLE_AMBASSADOR, "Brand Ambassador",
                Arrays.asList(
                        PermissionConstants.AMBASSADOR_DASHBOARD,
                        PermissionConstants.AMBASSADOR_TASKS,
                        PermissionConstants.AMBASSADOR_REWARDS),
                Arrays.asList(PermissionConstants.ROLE_MEMBER));

        // 3. RECRUITER (Inherits from MEMBER)
        createRole(PermissionConstants.ROLE_RECRUITER, "Company Recruiter",
                Arrays.asList(
                        PermissionConstants.JOB_POST,
                        PermissionConstants.JOB_MANAGE,
                        PermissionConstants.RESUME_VIEW,
                        PermissionConstants.APPLICANT_MANAGE,
                        PermissionConstants.COMPANY_BILLING),
                Arrays.asList(PermissionConstants.ROLE_MEMBER));

        // 4. OWNER (Inherits from RECRUITER)
        createRole(PermissionConstants.ROLE_OWNER, "Company Owner",
                Arrays.asList(
                        PermissionConstants.COMPANY_MANAGE,
                        PermissionConstants.COMPANY_USERS),
                Arrays.asList(PermissionConstants.ROLE_RECRUITER));

        // 5. APPLICANT (Inherits from MEMBER)
        createRole(PermissionConstants.ROLE_APPLICANT, "Job Applicant",
                Arrays.asList(
                        PermissionConstants.MEMBER_READ,
                        PermissionConstants.CONTENT_CREATE),
                Arrays.asList(PermissionConstants.ROLE_MEMBER));

        // 6. RECRUITER_MEMBER (Inherits from MEMBER)
        createRole(PermissionConstants.ROLE_RECRUITER_MEMBER, "Organization Recruiter Team Member",
                Arrays.asList(
                        PermissionConstants.JOB_POST,
                        PermissionConstants.JOB_MANAGE,
                        PermissionConstants.RESUME_VIEW,
                        PermissionConstants.APPLICANT_MANAGE,
                        PermissionConstants.ORG_VIEW),
                Arrays.asList(PermissionConstants.ROLE_MEMBER));

        // 7. ENTERPRISE_ADMIN (Inherits from OWNER)
        createRole(PermissionConstants.ROLE_ENTERPRISE_ADMIN, "Enterprise Organization Administrator",
                Arrays.asList(
                        PermissionConstants.ORG_MANAGE,
                        PermissionConstants.ORG_SETTINGS,
                        PermissionConstants.ORG_INVITE),
                Arrays.asList(PermissionConstants.ROLE_OWNER));

        // 8. PLATFORM_ADMIN (Inherits from SYSTEM_ADMIN)
        createRole(PermissionConstants.ROLE_PLATFORM_ADMIN, "Platform Administrator",
                Arrays.asList(
                        PermissionConstants.SYSTEM_MANAGE,
                        PermissionConstants.SYSTEM_VIEW_AUDIT),
                Arrays.asList(PermissionConstants.ROLE_SYSTEM_ADMIN));

        log.info("Role seeding complete.");
    }

    private void createRole(String name, String description, List<String> permissions, List<String> inheritsFrom) {
        RoleModel role = RoleModel.builder()
                .name(name)
                .description(description)
                .permissions(new ArrayList<>(permissions))
                .inheritsFrom(inheritsFrom != null ? new ArrayList<>(inheritsFrom) : new ArrayList<>())
                .build();
        roleRepository.save(role);
    }
}
