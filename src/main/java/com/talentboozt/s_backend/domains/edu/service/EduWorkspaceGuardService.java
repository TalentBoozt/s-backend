package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.repository.mongodb.EWorkspaceMembersRepository;
import com.talentboozt.s_backend.shared.tenant.TenantContext;
import org.springframework.stereotype.Service;

@Service
public class EduWorkspaceGuardService {

    private final EWorkspaceMembersRepository memberRepository;

    public EduWorkspaceGuardService(EWorkspaceMembersRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * Enforces that the given user is an active member of the specified workspace.
     * Throws if membership is not found.
     */
    public void enforceMembership(String workspaceId, String userId) {
        if (workspaceId == null || workspaceId.isEmpty() || "default".equals(workspaceId)) {
            return; // Public/marketplace context — no workspace isolation needed
        }

        boolean isMember = memberRepository.findByWorkspaceIdAndUserId(workspaceId, userId).isPresent();
        if (!isMember) {
            throw new RuntimeException("Access denied: You are not a member of this workspace.");
        }
    }

    /**
     * Validates workspace context from TenantContext thread-local if available.
     * Useful for service-level calls where workspace is implicitly set by the filter.
     */
    public void enforceCurrentContext(String userId) {
        TenantContext ctx = TenantContext.getCurrent();
        if (ctx != null && ctx.getWorkspaceId() != null) {
            enforceMembership(ctx.getWorkspaceId(), userId);
        }
    }

    /**
     * Verifies a resource belongs to the workspace set in TenantContext.
     * Used to prevent cross-workspace data leaks.
     */
    public void enforceResourceIsolation(String resourceWorkspaceId) {
        TenantContext ctx = TenantContext.getCurrent();
        if (ctx == null || ctx.getWorkspaceId() == null) {
            return; // No workspace context — marketplace mode
        }

        if (resourceWorkspaceId != null && !resourceWorkspaceId.isEmpty() 
            && !"default".equals(resourceWorkspaceId)
            && !ctx.getWorkspaceId().equals(resourceWorkspaceId)) {
            throw new RuntimeException("Access denied: Resource belongs to a different workspace.");
        }
    }
}
