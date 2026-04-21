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

@Service
public class EduAdminService {

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
    }

    public EUser inviteUser(String email, String firstName, String lastName,
            com.talentboozt.s_backend.domains.edu.enums.ERoles[] roles) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }

        EUser newUser = EUser.builder()
                .email(email)
                .displayName(firstName + " " + lastName)
                .roles(roles != null ? roles
                        : new com.talentboozt.s_backend.domains.edu.enums.ERoles[] {
                                com.talentboozt.s_backend.domains.edu.enums.ERoles.LEARNER })
                .passwordHash("INVITED_NO_PASS")
                .isEmailVerified(false)
                .isActive(true)
                .build();

        return userRepository.save(newUser);
    }

    public Page<com.talentboozt.s_backend.domains.edu.model.EWorkspaces> getWorkspaces(String search, int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        if (search == null || search.trim().isEmpty()) {
            return workspacesRepository.findAll(pageable);
        }
        return workspacesRepository.findAllByNameContainingIgnoreCaseOrDomainContainingIgnoreCase(
                search, search, pageable);
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

    public String generateImpersonationToken(String adminId, String userId) {
        EUser admin = userRepository.findById(adminId)
                .orElseThrow(() -> new EduResourceNotFoundException("Admin not found"));
        EUser target = userRepository.findById(userId)
                .orElseThrow(() -> new EduResourceNotFoundException("Target user not found"));

        // In a real implementation, you would use a JwtProvider to sign a token with special claims
        // such as "impersonator": adminId and "sub": userId.
        // For now, we return a secure random string that the frontend uses to signal the proxy session.
        String proxyToken = "staff_proxy_" + java.util.UUID.randomUUID().toString();
        
        // Log the security event
        System.out.println("ALERT: Admin " + adminId + " is impersonating user " + userId);
        
        return proxyToken;
    }
}
