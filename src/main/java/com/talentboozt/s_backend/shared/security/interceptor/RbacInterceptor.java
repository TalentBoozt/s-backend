package com.talentboozt.s_backend.shared.security.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talentboozt.s_backend.domains.subscription.application.entitlement.EntitlementResolutionResult;
import com.talentboozt.s_backend.domains.subscription.application.entitlement.UserEntitlement;
import com.talentboozt.s_backend.shared.security.annotations.RequirePlan;
import com.talentboozt.s_backend.shared.security.annotations.RequireRole;
import com.talentboozt.s_backend.shared.security.model.CustomUserDetails;
import com.talentboozt.s_backend.shared.security.model.EntitlementPlan;
import com.talentboozt.s_backend.shared.security.model.SecurityRole;
import com.talentboozt.s_backend.shared.security.port.EntitlementPort;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class RbacInterceptor implements HandlerInterceptor {

    private final EntitlementPort entitlementPort;
    private final ObjectMapper objectMapper;

    public RbacInterceptor(EntitlementPort entitlementPort, ObjectMapper objectMapper) {
        this.entitlementPort = entitlementPort;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        RequireRole roleAnnotation = handlerMethod.getMethodAnnotation(RequireRole.class);
        if (roleAnnotation == null) {
            roleAnnotation = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
        }

        RequirePlan planAnnotation = handlerMethod.getMethodAnnotation(RequirePlan.class);
        if (planAnnotation == null) {
            planAnnotation = handlerMethod.getBeanType().getAnnotation(RequirePlan.class);
        }

        if (roleAnnotation == null && planAnnotation == null) {
            return true;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Authentication required");
            return false;
        }

        Object principal = authentication.getPrincipal();
        String identifier = null;

        if (principal instanceof CustomUserDetails) {
            identifier = ((CustomUserDetails) principal).getUsername();
        } else if (principal instanceof UserDetails) {
            identifier = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            identifier = (String) principal;
        }

        if (identifier == null) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid authentication principal");
            return false;
        }

        EntitlementResolutionResult resolved = entitlementPort.resolveByEmail(identifier).orElse(null);
        if (resolved == null) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "User not found");
            return false;
        }

        UserEntitlement entitlements = resolved.userEntitlement();
        Set<String> roleNames = entitlements.roleNames();

        if (roleAnnotation != null) {
            SecurityRole[] requiredRoles = roleAnnotation.value();

            boolean hasRole;
            if (roleAnnotation.anyOf()) {
                hasRole = false;
                for (SecurityRole required : requiredRoles) {
                    if (roleNames.contains(required.name())) {
                        hasRole = true;
                        break;
                    }
                }
            } else {
                hasRole = true;
                for (SecurityRole required : requiredRoles) {
                    if (!roleNames.contains(required.name())) {
                        hasRole = false;
                        break;
                    }
                }
            }

            if (!hasRole) {
                sendErrorResponse(response, HttpStatus.FORBIDDEN, "Insufficient permissions. Role required.");
                return false;
            }
        }

        if (planAnnotation != null) {
            EntitlementPlan[] requiredPlans = planAnnotation.value();
            int currentTier = entitlements.subscription().planTierOrdinal();

            boolean hasPlan = false;
            for (EntitlementPlan required : requiredPlans) {
                if (currentTier >= required.ordinal()) {
                    hasPlan = true;
                    break;
                }
            }

            if (!hasPlan) {
                sendErrorResponse(response, HttpStatus.FORBIDDEN, "Upgrade required.");
                return false;
            }
        }

        return true;
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> errorData = new HashMap<>();
        errorData.put("error", message);
        errorData.put("status", String.valueOf(status.value()));
        objectMapper.writeValue(response.getWriter(), errorData);
    }
}
