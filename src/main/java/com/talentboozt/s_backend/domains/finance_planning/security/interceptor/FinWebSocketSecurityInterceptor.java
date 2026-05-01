package com.talentboozt.s_backend.domains.finance_planning.security.interceptor;

import com.talentboozt.s_backend.domains.finance_planning.security.rbac.FinPermission;
import com.talentboozt.s_backend.domains.finance_planning.services.FinSecurityService;
import com.talentboozt.s_backend.shared.security.model.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class FinWebSocketSecurityInterceptor implements ChannelInterceptor {
    private final FinSecurityService finSecurity;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null && StompCommand.SEND.equals(accessor.getCommand())) {
            String destination = accessor.getDestination();
            if (destination != null && destination.startsWith("/app/project/")) {
                // Extract projectId and organizationId from headers or destination
                String[] parts = destination.split("/");
                if (parts.length >= 4) {
                    String projectId = parts[3];
                    String orgId = accessor.getFirstNativeHeader("X-Organization-Id");
                    
                    if (orgId == null) {
                        log.warn("Missing organization ID in WebSocket message to {}", destination);
                        throw new SecurityException("Organization ID required");
                    }

                    // Determine required permission based on destination
                    FinPermission requiredPermission = FinPermission.READ_PROJECT;
                    if (destination.contains("/operation")) {
                        requiredPermission = FinPermission.WRITE_PROJECT;
                    }

                    if (!finSecurity.hasPermission(requiredPermission.name(), orgId, projectId)) {
                        log.warn("RBAC Denial: User lacks {} for project {}", requiredPermission, projectId);
                        throw new SecurityException("Permission denied");
                    }
                }
            }
        }
        
        return message;
    }
}
