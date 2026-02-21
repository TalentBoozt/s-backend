package com.talentboozt.s_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.talentboozt.s_backend.shared.security.model.CustomUserDetails;
import com.talentboozt.s_backend.shared.utils.JwtUtil;
import com.talentboozt.s_backend.domains.auth.service.CustomUserDetailsService;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import jakarta.servlet.ServletContext;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        HandshakeInterceptor handshakeInterceptor = new HandshakeInterceptor() {
            @Override
            public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                    WebSocketHandler wsHandler, Map<String, Object> attributes) {
                // Log essential headers to help diagnose proxy stripping
                // logger.info("WebSocket Handshake Attempt: {} | Upgrade: {} | Connection: {} |
                // Host: {}",
                // request.getURI(),
                // request.getHeaders().getUpgrade(),
                // request.getHeaders().getConnection(),
                // request.getHeaders().getHost());

                // Extract JWT token from "?token="
                String token = null;
                if (request.getURI().getQuery() != null) {
                    for (String param : request.getURI().getQuery().split("&")) {
                        if (param.startsWith("token=")) {
                            token = param.substring(6);
                            break;
                        }
                    }
                }

                // Fallback to natively reading HttpOnly cookies from the handshake headers
                if (token == null && request.getHeaders().get("Cookie") != null) {
                    for (String cookieHeader : request.getHeaders().get("Cookie")) {
                        for (String cookieStr : cookieHeader.split(";")) {
                            cookieStr = cookieStr.trim();
                            if (cookieStr.startsWith("TB_REFRESH=")) {
                                token = cookieStr.substring("TB_REFRESH=".length());
                                break;
                            } else if (cookieStr.startsWith("jwtToken=")) {
                                token = cookieStr.substring("jwtToken=".length());
                            }
                        }
                        if (token != null)
                            break;
                    }
                }

                if (token != null) {
                    try {
                        // We need access to JwtUtil and CustomUserDetailsService, they are beans.
                        if (request instanceof org.springframework.http.server.ServletServerHttpRequest) {
                            ServletContext servletContext = ((org.springframework.http.server.ServletServerHttpRequest) request)
                                    .getServletRequest().getServletContext();
                            ApplicationContext ctx = WebApplicationContextUtils
                                    .getWebApplicationContext(servletContext);
                            if (ctx != null) {
                                JwtUtil jwtUtil = ctx.getBean(JwtUtil.class);
                                CustomUserDetailsService userDetailsService = ctx
                                        .getBean(CustomUserDetailsService.class);
                                String username = jwtUtil.extractUsername(token);
                                if (username != null) {
                                    org.springframework.security.core.userdetails.UserDetails userDetails = userDetailsService
                                            .loadUserByUsername(username);
                                    if (jwtUtil.validateToken(token, userDetails.getUsername())
                                            && userDetails instanceof CustomUserDetails) {
                                        attributes.put("userId", ((CustomUserDetails) userDetails).getUserId());
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.warn("WebSocket token validation failed: " + e.getMessage());
                    }
                }

                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
                    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
                    attributes.put("userId", userDetails.getUserId());
                }

                if (request.getHeaders().getUpgrade() == null) {
                    logger.warn("CRITICAL: Upgrade header is MISSING from the request. WebSocket handshake will fail.");
                }
                return true;
            }

            @Override
            public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                    WebSocketHandler wsHandler, Exception exception) {
            }
        };

        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns(
                        "https://talnova.io",
                        "https://*.talnova.io",
                        "http://localhost:*",
                        "http://127.0.0.1:*")
                .addInterceptors(handshakeInterceptor);

        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns(
                        "https://talnova.io",
                        "https://*.talnova.io",
                        "http://localhost:*",
                        "http://127.0.0.1:*")
                .addInterceptors(handshakeInterceptor)
                .withSockJS()
                .setStreamBytesLimit(512 * 1024)
                .setHttpMessageCacheSize(1000)
                .setDisconnectDelay(30 * 1000);
    }
}
