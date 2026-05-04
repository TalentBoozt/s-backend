package com.talentboozt.s_backend.domains.finance_planning.security.interceptor;

import com.talentboozt.s_backend.domains.finance_planning.security.rbac.FinPermission;
import com.talentboozt.s_backend.domains.finance_planning.services.FinSecurityService;
import com.talentboozt.s_backend.shared.security.model.CustomUserDetails;
import com.talentboozt.s_backend.shared.utils.JwtUtil;
import com.talentboozt.s_backend.domains.auth.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class FinWebSocketSecurityInterceptor implements ChannelInterceptor {
    private final FinSecurityService finSecurity;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null) {
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                String authHeader = accessor.getFirstNativeHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    try {
                        String username = jwtUtil.extractUsername(token);
                        if (username != null) {
                            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                            if (jwtUtil.validateToken(token, userDetails.getUsername())) {
                                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities());
                                accessor.setUser(auth);
                                log.info("WebSocket user authenticated: {}", username);
                            }
                        }
                    } catch (Exception e) {
                        log.warn("WebSocket authentication failed: {}", e.getMessage());
                    }
                }
            } else if (StompCommand.SEND.equals(accessor.getCommand())) {
                String destination = accessor.getDestination();
                if (destination != null && destination.startsWith("/app/project/")) {
                    String[] parts = destination.split("/");
                    if (parts.length >= 4) {
                        String projectId = parts[3];
                        String orgId = accessor.getFirstNativeHeader("X-Organization-Id");
                        
                        if (orgId == null) {
                            log.warn("Missing organization ID in WebSocket message to {}", destination);
                            throw new SecurityException("Organization ID required");
                        }

                        // Temporarily set SecurityContext for FinSecurityService
                        Authentication auth = (Authentication) accessor.getUser();
                        if (auth != null) {
                            SecurityContextHolder.getContext().setAuthentication(auth);
                        }

                        try {
                            FinPermission requiredPermission = FinPermission.READ_PROJECT;
                            if (destination.contains("/operation")) {
                                requiredPermission = FinPermission.WRITE_PROJECT;
                            }

                            if (!finSecurity.hasPermission(requiredPermission.name(), orgId, projectId)) {
                                log.warn("RBAC Denial: User lacks {} for project {}", requiredPermission, projectId);
                                throw new SecurityException("Permission denied");
                            }
                        } finally {
                            SecurityContextHolder.clearContext();
                        }
                    }
                }
            }
        }
        
        return message;
    }
}
