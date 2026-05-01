package com.talentboozt.s_backend.domains.finance_planning.security.aspect;

import com.talentboozt.s_backend.domains.finance_planning.security.annotations.RequiresFinPermission;
import com.talentboozt.s_backend.domains.finance_planning.services.FinSecurityService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
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
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        
        String orgId = extractValue(request, requiresPermission.orgIdSource(), requiresPermission.orgIdKey());
        String projectId = extractValue(request, requiresPermission.projectIdSource(), requiresPermission.projectIdKey());

        if (orgId == null || orgId.isEmpty()) {
            throw new SecurityException("Organization ID is required for this operation");
        }

        boolean hasPermission = finSecurity.hasPermission(requiresPermission.value().name(), orgId, projectId);
        if (!hasPermission) {
            throw new SecurityException("Access Denied: Insufficient permissions for " + requiresPermission.value());
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
