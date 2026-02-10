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
                logger.info("WebSocket Handshake Attempt: {} | Upgrade: {} | Connection: {} | Host: {}",
                        request.getURI(),
                        request.getHeaders().getUpgrade(),
                        request.getHeaders().getConnection(),
                        request.getHeaders().getHost());

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
