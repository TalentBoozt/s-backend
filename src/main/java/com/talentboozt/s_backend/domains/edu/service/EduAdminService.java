package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.enums.EReportStatus;
import com.talentboozt.s_backend.domains.edu.exception.EduResourceNotFoundException;
import com.talentboozt.s_backend.domains.edu.model.EUser;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class EduAdminService {

    @Autowired
    private com.talentboozt.s_backend.domains.auth.service.CredentialsService credentialsService;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Autowired
    private org.springframework.mail.javamail.JavaMailSender mailSender;

    @Autowired
    private EduSubscriptionService subscriptionService;

    private final ECoursesRepository coursesRepository;
    private final EEnrollmentsRepository enrollmentsRepository;
    private final EUserRepository userRepository;
    private final ETransactionsRepository transactionsRepository;
    private final EReportsRepository reportsRepository;
    private final EWorkspacesRepository workspacesRepository;
    private final EWorkspaceMembersRepository memberRepository;

    public EduAdminService(ECoursesRepository coursesRepository,
            EEnrollmentsRepository enrollmentsRepository,
            EUserRepository userRepository,
            ETransactionsRepository transactionsRepository,
            EReportsRepository reportsRepository,
            EWorkspacesRepository workspacesRepository,
            EWorkspaceMembersRepository memberRepository) {
        this.coursesRepository = coursesRepository;
        this.enrollmentsRepository = enrollmentsRepository;
        this.userRepository = userRepository;
        this.transactionsRepository = transactionsRepository;
        this.reportsRepository = reportsRepository;
        this.workspacesRepository = workspacesRepository;
        this.memberRepository = memberRepository;
    }

    public Map<String, Object> getGlobalStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalCourses", coursesRepository.count());
        stats.put("totalEnrollments", enrollmentsRepository.count());
        stats.put("totalUsers", userRepository.count());

        // Sum total revenue from transactions
        double totalRevenue = transactionsRepository.findAll().stream()
                .mapToDouble(t -> t.getAmount() != null ? t.getAmount() : 0.0)
                .sum();
        stats.put("totalRevenue", totalRevenue);

        double platformEarnings = transactionsRepository.findAll().stream()
                .mapToDouble(t -> t.getPlatformFee() != null ? t.getPlatformFee() : 0.0)
                .sum();
        stats.put("platformEarnings", platformEarnings);
        stats.put("pendingModeration", reportsRepository.findByStatus(EReportStatus.PENDING).size());
        stats.put("systemHealth", 100);

        return stats;
    }

    public Page<EUser> getUsers(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        if (search == null || search.trim().isEmpty()) {
            return userRepository.findAll(pageable);
        }
        // Support role-based search by exact string if the search starts with "role:"
        if (search.startsWith("role:")) {
            String roleName = search.substring(5).toUpperCase();
            // This is a simplification; ideally use a custom query for array containing
            return userRepository.findAll(pageable);
        }
        return userRepository.findAllByEmailContainingIgnoreCaseOrDisplayNameContainingIgnoreCase(
                search, search, pageable);
    }

    public void updateUserStatus(String userId, Boolean banned, Boolean active, String reason) {
        EUser user = userRepository.findById(userId)
                .orElseThrow(() -> new EduResourceNotFoundException("User not found with id: " + userId));

        if (banned != null) {
            user.setIsBanned(banned);
            if (banned) {
                user.setBanReason(reason);
            }
        }
        if (active != null) {
            user.setIsActive(active);
        }

        userRepository.save(user);
    }

    public void updateUserRoles(String userId, com.talentboozt.s_backend.domains.edu.enums.ERoles[] roles) {
        EUser user = userRepository.findById(userId)
                .orElseThrow(() -> new EduResourceNotFoundException("User not found with id: " + userId));
        user.setRoles(roles);
        userRepository.save(user);

        // Synchronize with global credentials
        credentialsService.updateUserRoles(userId, java.util.Arrays.stream(roles).map(Enum::name).collect(java.util.stream.Collectors.toList()));
    }

    public EUser inviteUser(String email, String firstName, String lastName,
            com.talentboozt.s_backend.domains.edu.enums.ERoles[] roles) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }

        String userIdToUse;
        String defaultPassword = java.util.UUID.randomUUID().toString().substring(0, 8);
        String encryptedPass = defaultPassword;
        try {
            encryptedPass = com.talentboozt.s_backend.shared.utils.EncryptionUtility.encrypt(defaultPassword);
        } catch (Exception e) {
            // Ignore
        }

        com.talentboozt.s_backend.domains.auth.model.CredentialsModel globalCreds = credentialsService.getCredentialsByEmail(email);
        if (globalCreds != null) {
            userIdToUse = globalCreds.getEmployeeId();
            credentialsService.updatePassword(globalCreds.getId(), encryptedPass);
            java.util.List<String> newRolesList = java.util.Arrays.stream(roles).map(Enum::name).collect(java.util.stream.Collectors.toList());
            credentialsService.updateUserRoles(userIdToUse, newRolesList);
        } else {
            com.talentboozt.s_backend.domains.auth.model.CredentialsModel newCreds = com.talentboozt.s_backend.domains.auth.model.CredentialsModel
                    .builder()
                    .email(email)
                    .password(encryptedPass)
                    .firstname(firstName)
                    .lastname(lastName)
                    .roles(java.util.Arrays.stream(roles).map(Enum::name).collect(java.util.stream.Collectors.toList()))
                    .platformRole("USER")
                    .registeredFrom("EDU_PLATFORM")
                    .build();
            newCreds = credentialsService.addCredentials(newCreds, "EDU_PLATFORM", null);
            userIdToUse = newCreds.getEmployeeId();
        }

        EUser newUser = EUser.builder()
                .id(userIdToUse)
                .email(email)
                .displayName(firstName + " " + lastName)
                .roles(roles != null ? roles
                        : new com.talentboozt.s_backend.domains.edu.enums.ERoles[] {
                                com.talentboozt.s_backend.domains.edu.enums.ERoles.LEARNER })
                .passwordHash(passwordEncoder.encode(defaultPassword))
                .isEmailVerified(false)
                .isActive(true)
                .build();

        EUser savedUser = userRepository.save(newUser);

        // Send email with credentials/invitation
        sendInvitationEmail(email, firstName, lastName, defaultPassword);

        return savedUser;
    }

    private void sendInvitationEmail(String to, String firstName, String lastName, String defaultPassword) {
        try {
            jakarta.mail.internet.MimeMessage message = mailSender.createMimeMessage();
            org.springframework.mail.javamail.MimeMessageHelper helper = new org.springframework.mail.javamail.MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject("Invitation to Talnova Enterprise");

            String htmlBody;
            if (defaultPassword != null) {
                htmlBody = "<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\" />\n" +
                        "    <title>Talnova Invitation</title>\n" +
                        "</head>\n" +
                        "<body style=\"font-family: Arial, sans-serif; background-color: #F5F7F9; color: #3D3D3D; margin: 0;\">\n" +
                        "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"max-width: 600px; margin: auto; background: #fff; border-radius: 8px; overflow: hidden;\">\n" +
                        "    <tr style=\"background: linear-gradient(135deg, #ff007f, #00e5ff);\">\n" +
                        "        <td style=\"padding: 20px; text-align: center;\">\n" +
                        "            <img src=\"https://talnova.io/assets/logos/g-l-w.avif\" alt=\"Talnova\" width=\"120\"/>\n" +
                        "        </td>\n" +
                        "    </tr>\n" +
                        "    <tr>\n" +
                        "        <td style=\"padding: 30px;\">\n" +
                        "            <h2 style=\"color: #ff007f;\">Welcome to Talnova! 🎉</h2>\n" +
                        "            <p>Hello " + firstName + " " + lastName + ",</p>\n" +
                        "            <p>You have been invited as an Enterprise Admin on the Talnova platform.</p>\n" +
                        "            <p>Here are your temporary login credentials:</p>\n" +
                        "            <p><strong>Email:</strong> " + to + "</p>\n" +
                        "            <p><strong>Temporary Password:</strong> " + defaultPassword + "</p>\n" +
                        "            <p>Please change your password immediately upon your first login.</p>\n" +
                        "            <p style=\"text-align: center; margin: 30px 0;\">\n" +
                        "                <a href=\"https://edu.talnova.io/login\" style=\"background: linear-gradient(135deg, #ff007f, #00e5ff); color: white; padding: 12px 20px; text-decoration: none; border-radius: 5px; font-weight: bold;\">Log In Now</a>\n" +
                        "            </p>\n" +
                        "            <p style=\"margin-top: 30px;\">– Team Talnova</p>\n" +
                        "        </td>\n" +
                        "    </tr>\n" +
                        "    <tr style=\"background-color: #EAEEF2;\">\n" +
                        "        <td style=\"padding: 20px; text-align: center; font-size: 12px; color: #7D7D7D;\">\n" +
                        "            © " + java.time.Year.now().getValue() + " Talnova. All rights reserved.\n" +
                        "        </td>\n" +
                        "    </tr>\n" +
                        "</table>\n" +
                        "</body>\n" +
                        "</html>";
            } else {
                htmlBody = "<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\" />\n" +
                        "    <title>Talnova Invitation</title>\n" +
                        "</head>\n" +
                        "<body style=\"font-family: Arial, sans-serif; background-color: #F5F7F9; color: #3D3D3D; margin: 0;\">\n" +
                        "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"max-width: 600px; margin: auto; background: #fff; border-radius: 8px; overflow: hidden;\">\n" +
                        "    <tr style=\"background: linear-gradient(135deg, #ff007f, #00e5ff);\">\n" +
                        "        <td style=\"padding: 20px; text-align: center;\">\n" +
                        "            <img src=\"https://talnova.io/assets/logos/g-l-w.avif\" alt=\"Talnova\" width=\"120\"/>\n" +
                        "        </td>\n" +
                        "    </tr>\n" +
                        "    <tr>\n" +
                        "        <td style=\"padding: 30px;\">\n" +
                        "            <h2 style=\"color: #ff007f;\">Welcome back to Talnova! 🎉</h2>\n" +
                        "            <p>Hello " + firstName + " " + lastName + ",</p>\n" +
                        "            <p>You have been assigned as an Enterprise Admin for a new workspace on the Talnova platform.</p>\n" +
                        "            <p>Since you already have a Talnova account, you can log in using your existing credentials:</p>\n" +
                        "            <p><strong>Email:</strong> " + to + "</p>\n" +
                        "            <p style=\"text-align: center; margin: 30px 0;\">\n" +
                        "                <a href=\"https://edu.talnova.io/login\" style=\"background: linear-gradient(135deg, #ff007f, #00e5ff); color: white; padding: 12px 20px; text-decoration: none; border-radius: 5px; font-weight: bold;\">Log In Now</a>\n" +
                        "            </p>\n" +
                        "            <p style=\"margin-top: 30px;\">– Team Talnova</p>\n" +
                        "        </td>\n" +
                        "    </tr>\n" +
                        "    <tr style=\"background-color: #EAEEF2;\">\n" +
                        "        <td style=\"padding: 20px; text-align: center; font-size: 12px; color: #7D7D7D;\">\n" +
                        "            © " + java.time.Year.now().getValue() + " Talnova. All rights reserved.\n" +
                        "        </td>\n" +
                        "    </tr>\n" +
                        "</table>\n" +
                        "</body>\n" +
                        "</html>";
            }

            helper.setText(htmlBody, true);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send invitation email to " + to + ": " + e.getMessage());
        }
    }

    public Page<com.talentboozt.s_backend.domains.edu.model.EWorkspaces> getWorkspaces(String search, int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        if (search == null || search.trim().isEmpty()) {
            return workspacesRepository.findAll(pageable);
        }
        return workspacesRepository.findAllByNameContainingIgnoreCaseOrDomainContainingIgnoreCaseOrSlugContainingIgnoreCase(
                search, search, search, pageable);
    }

    public void updateWorkspaceStatus(String workspaceId, Boolean active) {
        com.talentboozt.s_backend.domains.edu.model.EWorkspaces workspace = workspacesRepository.findById(workspaceId)
                .orElseThrow(() -> new EduResourceNotFoundException("Workspace not found with id: " + workspaceId));
        if (active != null) {
            workspace.setIsActive(active);
        }
        workspacesRepository.save(workspace);
    }

    public void updateWorkspaceTier(String workspaceId, String plan) {
        com.talentboozt.s_backend.domains.edu.model.EWorkspaces workspace = workspacesRepository.findById(workspaceId)
                .orElseThrow(() -> new EduResourceNotFoundException("Workspace not found with id: " + workspaceId));
        if (plan != null) {
            workspace.setPlan(com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan.valueOf(plan));
        }
        workspacesRepository.save(workspace);
    }

    public void verifyWorkspace(String workspaceId) {
        com.talentboozt.s_backend.domains.edu.model.EWorkspaces workspace = workspacesRepository.findById(workspaceId)
                .orElseThrow(() -> new EduResourceNotFoundException("Workspace not found with id: " + workspaceId));

        // Logical verification - could involve checking provided business documents
        workspace.setIsActive(true);
        if (workspace.getSettings() != null) {
            workspace.getSettings().setIsBrandingEnabled(true);
        }
        workspacesRepository.save(workspace);
    }

    public Map<String, Object> getWorkspaceCompliance(String workspaceId) {
        com.talentboozt.s_backend.domains.edu.model.EWorkspaces workspace = workspacesRepository.findById(workspaceId)
                .orElseThrow(() -> new EduResourceNotFoundException("Workspace not found with id: " + workspaceId));

        long currentMembers = memberRepository.findByWorkspaceId(workspaceId).size();
        long currentCourses = coursesRepository.count(); // Ideally filtered by workspace if courses had workspaceId

        Map<String, Object> report = new HashMap<>();
        report.put("workspaceId", workspaceId);
        report.put("name", workspace.getName());
        report.put("plan", workspace.getPlan());
        report.put("memberLimit", workspace.getMaxMembers());
        report.put("currentMembers", currentMembers);
        report.put("isOverMemberLimit", currentMembers > workspace.getMaxMembers());
        report.put("status", workspace.getIsActive() ? "HEALTHY" : "SUSPENDED");
        report.put("domainVerified", workspace.getDomain() != null);

        return report;
    }

    public com.talentboozt.s_backend.domains.edu.model.EWorkspaces registerWorkspace(Map<String, Object> data) {
        String name = (String) data.get("name");
        String domain = (String) data.get("domain");
        String slug = (String) data.get("slug");
        String email = (String) data.get("email");
        String planStr = (String) data.get("plan");
        Integer maxMembers = (Integer) data.get("maxMembers");

        // 1. Find or create the owner
        EUser owner;
        boolean isNewUser = false;
        java.util.Optional<EUser> existingOwnerOpt = userRepository.findByEmail(email);
        if (existingOwnerOpt.isEmpty()) {
            owner = inviteUser(email, name, "Admin", new com.talentboozt.s_backend.domains.edu.enums.ERoles[] {
                    com.talentboozt.s_backend.domains.edu.enums.ERoles.ENTERPRISE_ADMIN
            });
            isNewUser = true;
        } else {
            owner = existingOwnerOpt.get();
        }

        // Ensure owner has ENTERPRISE_ADMIN role
        boolean hasAdminRole = false;
        if (owner.getRoles() != null) {
            for (com.talentboozt.s_backend.domains.edu.enums.ERoles r : owner.getRoles()) {
                if (r == com.talentboozt.s_backend.domains.edu.enums.ERoles.ENTERPRISE_ADMIN) {
                    hasAdminRole = true;
                    break;
                }
            }
        }

        java.util.List<com.talentboozt.s_backend.domains.edu.enums.ERoles> updatedRoles = new java.util.ArrayList<>();
        if (owner.getRoles() != null) {
            updatedRoles.addAll(java.util.Arrays.asList(owner.getRoles()));
        }
        if (!hasAdminRole) {
            updatedRoles.add(com.talentboozt.s_backend.domains.edu.enums.ERoles.ENTERPRISE_ADMIN);
            owner.setRoles(updatedRoles.toArray(new com.talentboozt.s_backend.domains.edu.enums.ERoles[0]));
            userRepository.save(owner);
            credentialsService.updateUserRoles(owner.getId(), updatedRoles.stream().map(Enum::name).collect(java.util.stream.Collectors.toList()));
        }

        // If the user already existed in our database, we should still reset/generate a default password
        // and send them the invitation email. This ensures the admin credentials (email & password)
        // are always sent to the enterprise owner.
        if (!isNewUser) {
            String defaultPassword = java.util.UUID.randomUUID().toString().substring(0, 8);
            String encryptedPass = defaultPassword;
            try {
                encryptedPass = com.talentboozt.s_backend.shared.utils.EncryptionUtility.encrypt(defaultPassword);
            } catch (Exception e) {
                // Ignore
            }

            // Update globally
            com.talentboozt.s_backend.domains.auth.model.CredentialsModel globalCreds = credentialsService.getCredentialsByEmail(email);
            if (globalCreds != null) {
                credentialsService.updatePassword(globalCreds.getId(), encryptedPass);
                java.util.List<String> newRolesStr = updatedRoles.stream().map(Enum::name).collect(java.util.stream.Collectors.toList());
                credentialsService.updateUserRoles(owner.getId(), newRolesStr);
            }

            // Update locally
            owner.setPasswordHash(passwordEncoder.encode(defaultPassword));
            userRepository.save(owner);

            // Send invitation email with the temporary password
            sendInvitationEmail(email, name, "Admin", defaultPassword);
        }

        // 2. Create the workspace
        com.talentboozt.s_backend.domains.edu.model.EWorkspaces workspace = com.talentboozt.s_backend.domains.edu.model.EWorkspaces
                .builder()
                .name(name)
                .domain(domain)
                .slug(slug)
                .ownerId(owner.getId())
                .plan(com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan.valueOf(planStr))
                .maxMembers(maxMembers != null ? maxMembers : 100)
                .isActive(true)
                .type(com.talentboozt.s_backend.domains.edu.enums.EWorkspaceType.ORGANIZATION)
                .build();

        com.talentboozt.s_backend.domains.edu.model.EWorkspaces savedWorkspace = workspacesRepository.save(workspace);

        // Provision/Sync subscription for owner
        com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan workspacePlan = com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan.valueOf(planStr);
        if (workspacePlan == com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan.ENTERPRISE) {
            subscriptionService.provisionManualEnterprise(owner.getId(), 0.0, Integer.MAX_VALUE, maxMembers != null ? maxMembers : 100, "Enterprise Workspace Setup");
        } else {
            subscriptionService.syncUserRoles(owner.getId(), workspacePlan);
        }

        // Auto-assign owner as Admin member natively
        com.talentboozt.s_backend.domains.edu.model.EWorkspaceMembers ownerMember = com.talentboozt.s_backend.domains.edu.model.EWorkspaceMembers.builder()
                .workspaceId(savedWorkspace.getId())
                .userId(owner.getId())
                .role(com.talentboozt.s_backend.domains.edu.enums.ERoles.ENTERPRISE_ADMIN)
                .status("ACTIVE")
                .joinedAt(java.time.Instant.now())
                .lastActiveAt(java.time.Instant.now())
                .createdBy("SYSTEM")
                .build();
        memberRepository.save(ownerMember);

        return savedWorkspace;
    }

    public String generateImpersonationToken(String adminId, String userId) {
        EUser admin = userRepository.findById(adminId)
                .orElseThrow(() -> new EduResourceNotFoundException("Admin not found"));
        EUser target = userRepository.findById(userId)
                .orElseThrow(() -> new EduResourceNotFoundException("Target user not found"));

        // In a real implementation, you would use a JwtProvider to sign a token with
        // special claims
        // such as "impersonator": adminId and "sub": userId.
        // For now, we return a secure random string that the frontend uses to signal
        // the proxy session.
        String proxyToken = "staff_proxy_" + java.util.UUID.randomUUID().toString();

        // Log the security event
        System.out.println("ALERT: Admin " + adminId + " is impersonating user " + userId);

        return proxyToken;
    }
}
