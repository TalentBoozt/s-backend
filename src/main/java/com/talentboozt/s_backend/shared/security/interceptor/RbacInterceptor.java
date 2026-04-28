package com.talentboozt.s_backend.shared.security.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talentboozt.s_backend.domains.edu.enums.ERoles;
import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;
import com.talentboozt.s_backend.domains.edu.model.EUser;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EUserRepository;
import com.talentboozt.s_backend.domains.subscription.model.Subscription;
import com.talentboozt.s_backend.domains.subscription.service.SubscriptionService;
import com.talentboozt.s_backend.shared.security.annotations.RequirePlan;
import com.talentboozt.s_backend.shared.security.annotations.RequireRole;
import com.talentboozt.s_backend.shared.security.model.CustomUserDetails;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class RbacInterceptor implements HandlerInterceptor {

    private final EUserRepository userRepository;
    private final SubscriptionService subscriptionService;
    private final ObjectMapper objectMapper;

    public RbacInterceptor(EUserRepository userRepository, SubscriptionService subscriptionService, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.subscriptionService = subscriptionService;
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

        // The CustomUserDetails.getUsername() returns the email based on CustomUserDetailsService
        Optional<EUser> userOpt = userRepository.findByEmail(identifier);
        
        if (userOpt.isEmpty()) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "User not found");
            return false;
        }

        EUser user = userOpt.get();

        // 1. Check Roles
        if (roleAnnotation != null) {
            ERoles[] requiredRoles = roleAnnotation.value();
            ERoles[] userRoles = user.getRoles() != null ? user.getRoles() : new ERoles[0];
            
            boolean hasRole = false;
            if (roleAnnotation.anyOf()) {
                for (ERoles required : requiredRoles) {
                    if (Arrays.asList(userRoles).contains(required)) {
                        hasRole = true;
                        break;
                    }
                }
            } else {
                hasRole = true;
                for (ERoles required : requiredRoles) {
                    if (!Arrays.asList(userRoles).contains(required)) {
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

        // 2. Check Plan
        if (planAnnotation != null) {
            ESubscriptionPlan[] requiredPlans = planAnnotation.value();
            Subscription subscription = subscriptionService.getActiveSubscription(user.getId());
            ESubscriptionPlan currentPlan = subscription != null ? subscription.getPlan() : ESubscriptionPlan.FREE;

            boolean hasPlan = false;
            for (ESubscriptionPlan required : requiredPlans) {
                if (currentPlan.ordinal() >= required.ordinal()) {
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
