package com.talentboozt.s_backend.Shared;

import com.talentboozt.s_backend.Model.common.auth.CredentialsModel;
import com.talentboozt.s_backend.Repository.SYS_TRACKING.TrackingEventRepository;
import com.talentboozt.s_backend.Service._private.IpTimeZoneService;
import com.talentboozt.s_backend.Service._private.RateLimiterService;
import com.talentboozt.s_backend.Service._private.TimeZoneMismatchService;
import com.talentboozt.s_backend.Service._private.UserActivityService;
import com.talentboozt.s_backend.Service.common.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class IpCaptureFilter extends OncePerRequestFilter {

    @Autowired
    private UserActivityService userActivityService;
    @Autowired
    private RateLimiterService rateLimiterService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private IpTimeZoneService ipTimeZoneService;
    @Autowired
    private TimeZoneMismatchService timeZoneMisMatchService;

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip != null ? ip : "0.0.0.0";
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String endpointAccessed = request.getRequestURI();

        // Skip logging for actuator metrics endpoint
        if (endpointAccessed.startsWith("/actuator/metrics")) {
            filterChain.doFilter(request, response);
            return;
        }

        String ipAddress = getClientIp(request);

        // Extract JWT token from Authorization header
        String token = extractJwtFromRequest(request);

        String userId = "Anonymous";
        if (token != null && jwtService.validateToken(token)) {
            // Get user info from the JWT token
            try {
                CredentialsModel user = jwtService.getUserFromToken(token);
                userId = user.getEmployeeId(); // Use the userId from the JWT token
            } catch (Exception e) {
                // Log and proceed with "anonymous" in case of any issues with the token
                System.out.println("Error extracting user from JWT: " + e.getMessage());
            }
        }

        // Check rate-limiting: If the user/IP exceeds the limit, we handle it accordingly
        boolean allowed = rateLimiterService.checkRateLimit(ipAddress);
        if (!allowed) {
            response.setStatus(429); // 429 Too Many Requests
            return;
        }

        // Log the activity
        userActivityService.logUserActivity(userId, ipAddress, endpointAccessed);

        // Extract session ID from request headers or cookies
        String sessionId = extractSessionIdFromRequest(request);
        String userAgent = extractUserAgentFromRequest(request);
        Integer offset = extractOffsetFromRequest(request);

        // Detect timezone mismatch
        boolean isTimeZoneMismatch = false;
        if (offset != null) {
            isTimeZoneMismatch = timeZoneMisMatchService.isTimeZoneMismatch(ipAddress, offset);
        }

        // Enrich session with timezone information
        ipTimeZoneService.enrichSessionWithTimeZone(sessionId, userAgent, ipAddress, isTimeZoneMismatch);

        // Optionally, log or handle the timezone mismatch
        if (isTimeZoneMismatch) {
            // Handle mismatch (e.g., log, alert, etc.)
            System.out.println("Timezone mismatch detected for session: " + sessionId);
        }

        filterChain.doFilter(request, response);
    }

    // Helper method to extract JWT from the Authorization header
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Extract token after "Bearer "
        }
        return null; // No token found
    }

    // Helper method to extract session ID from request headers or cookies
    private String extractSessionIdFromRequest(HttpServletRequest request) {
        String sessionId = request.getHeader("X-Session-Id");

        if (sessionId == null) {
            sessionId = request.getParameter("sessionId");
        }

        return sessionId;
    }

    private String extractUserAgentFromRequest(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    private Integer extractOffsetFromRequest(HttpServletRequest request) {
        try {
            String offsetHeader = request.getHeader("X-Offset");
            return offsetHeader != null ? Integer.parseInt(offsetHeader) : null;
        } catch (NumberFormatException e) {
            return null; // fallback to no mismatch detection
        }
    }
}

