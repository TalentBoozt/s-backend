package com.talentboozt.s_backend.shared.security.utils;

import com.talentboozt.s_backend.shared.security.model.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    @Autowired
    private HttpServletRequest request;

    public String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        }
        // Fallback for immediate detection/onboarding phases
        return request.getHeader("x-user-id");
    }

    public String getCurrentWorkspaceId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            String workspaceId = ((CustomUserDetails) authentication.getPrincipal()).getActiveWorkspaceId();
            if (workspaceId != null) return workspaceId;
        }
        // Fallback to Header provided by Frontend apiClient
        return request.getHeader("x-workspace-id");
    }
}
