package com.talentboozt.s_backend.domains.finance_planning.security.aspect;

import com.talentboozt.s_backend.domains.finance_planning.security.annotations.RequiresFinPermission;
import com.talentboozt.s_backend.domains.finance_planning.services.FinSecurityService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

@Aspect
@Component
@RequiredArgsConstructor
public class FinSecurityAspect {
    private final FinSecurityService finSecurity;

    @Before("@annotation(requiresPermission)")
    public void checkPermission(JoinPoint joinPoint, RequiresFinPermission requiresPermission) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return; // Not in a request context, skip check (e.g. internal calls or tests)
        }
        
        HttpServletRequest request = attributes.getRequest();
        
        String orgId = extractValue(request, requiresPermission.orgIdSource(), requiresPermission.orgIdKey());
        String projectId = extractValue(request, requiresPermission.projectIdSource(), requiresPermission.projectIdKey());

        if (orgId == null || orgId.isEmpty()) {
            throw new AccessDeniedException("Organization ID is required for this operation");
        }

        // Check authentication first
        if (!finSecurity.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("Authentication is required to access this resource");
        }

        boolean hasPermission = finSecurity.hasPermission(requiresPermission.value().name(), orgId, projectId);
        if (!hasPermission) {
            throw new AccessDeniedException("Access Denied: Insufficient permissions for " + requiresPermission.value());
        }
    }

    private String extractValue(HttpServletRequest request, String source, String key) {
        if ("none".equalsIgnoreCase(source)) return null;
        
        switch (source.toLowerCase()) {
            case "header":
                return request.getHeader(key);
            case "path":
                @SuppressWarnings("unchecked")
                Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
                return pathVariables != null ? pathVariables.get(key) : null;
            case "param":
                return request.getParameter(key);
            default:
                return null;
        }
    }
}
