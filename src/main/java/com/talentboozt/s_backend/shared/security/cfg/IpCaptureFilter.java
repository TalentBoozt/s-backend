package com.talentboozt.s_backend.shared.security.cfg;

import com.talentboozt.s_backend.domains.auth.model.CredentialsModel;
import com.talentboozt.s_backend.domains.audit_logs.service.ClientActAuditLogService;
import com.talentboozt.s_backend.shared.security.service.IpTimeZoneService;
import com.talentboozt.s_backend.shared.security.service.RateLimiterService;
import com.talentboozt.s_backend.shared.security.service.TimeZoneMismatchService;
import com.talentboozt.s_backend.domains._private.service.UserActivityService;
import com.talentboozt.s_backend.shared.security.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

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
    @Autowired
    private ClientActAuditLogService clientActAuditLogService;

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.split(",")[0].trim(); // Use first IP
        }

        ip = request.getHeader("Proxy-Client-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        ip = request.getHeader("WL-Proxy-Client-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        return request.getRemoteAddr() != null ? request.getRemoteAddr() : "0.0.0.0";
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
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
                Map<String, Object> detail = new HashMap<>();
                detail.put("token", token);
                detail.put("error", e.getMessage());
                clientActAuditLogService.log("Anonymous", ipAddress, null, "INVALID_JWT", "IpCaptureFilter", detail);
            }
        } else if (token != null) {
            clientActAuditLogService.log("Anonymous", ipAddress, null, "INVALID_OR_EXPIRED_JWT", "IpCaptureFilter", safeMapOf("token", token));
        } else {
            clientActAuditLogService.log("Anonymous", ipAddress, null, "ANONYMOUS_ACCESS", "IpCaptureFilter", safeMapOf("uri", endpointAccessed));
        }

        // Check rate-limiting: If the user/IP exceeds the limit, we handle it accordingly
        String uri = request.getRequestURI();
        String category = categorizeEndpoint(uri); // custom function
        String rateLimitKey = userId.equals("Anonymous") ? ipAddress : ipAddress + ":" + userId;
        boolean allowed = rateLimiterService.checkRateLimit(rateLimitKey, category);
        if (!allowed) {
            Map<String, Object> detail = new HashMap<>();
            detail.put("category", category);
            detail.put("ipAddress", ipAddress);
            detail.put("userId", userId);
            detail.put("uri", uri);
            detail.put("rateLimitKey", rateLimitKey);
            clientActAuditLogService.log(userId, ipAddress, null, "RATE_LIMIT_EXCEEDED", "IpCaptureFilter", detail);
            response.setStatus(429); // 429 Too Many Requests
            response.setHeader("Retry-After", "60");
            return;
        }

        // Log the activity
        userActivityService.logUserActivity(userId, ipAddress, endpointAccessed);

        // Extract session ID from request headers or cookies
        String sessionId = extractSessionIdFromRequest(request);
        String userAgent = extractUserAgentFromRequest(request);
        Integer offset = extractOffsetFromRequest(request);
        boolean captcha = captchaVerified(request);

        if (captcha) {
            clientActAuditLogService.log(userId, ipAddress, sessionId, "CAPTCHA_PREVIOUSLY_VERIFIED", "IpCaptureFilter", safeMapOf("userAgent", userAgent));
        }

        if (offset == null) {
            clientActAuditLogService.log(userId, ipAddress, sessionId, "OFFSET_NOT_PROVIDED", "IpCaptureFilter", safeMapOf("userAgent", userAgent));
        }

        // Detect timezone mismatch
        boolean isTimeZoneMismatch = false;
        if (offset != null) {
            isTimeZoneMismatch = timeZoneMisMatchService.isTimeZoneMismatch(ipAddress, offset);
        }

        // Enrich session with timezone information
        ipTimeZoneService.enrichSessionWithTimeZone(sessionId, userAgent, ipAddress, isTimeZoneMismatch);

        if (isTimeZoneMismatch && !captcha) {
            Map<String, Object> detail = new HashMap<>();
            detail.put("clientOffset", offset);
            detail.put("vpnDetected", isTimeZoneMismatch);
            detail.put("captchaVerified", false);
            detail.put("userAgent", extractUserAgentFromRequest(request));

            clientActAuditLogService.log(userId, ipAddress, sessionId, "TIMEZONE_MISMATCH", "IpCaptureFilter", detail);
            response.setHeader("X-Timezone-Mismatch", "true");
        }

        filterChain.doFilter(request, response);
    }

    private static final Map<String, String> CATEGORY_MAP = Map.of(
            "/api/event/track", "analytics",
            "/api/auth", "auth",
            "/api/user", "user",
            "/api/public", "public"
    );

    private String categorizeEndpoint(String uri) {
        return CATEGORY_MAP.entrySet()
                .stream()
                .filter(entry -> uri.startsWith(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse("default");
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

    private boolean captchaVerified(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Boolean verified = (Boolean) session.getAttribute("captchaVerified");
            Instant verifiedAt = (Instant) session.getAttribute("captchaVerifiedAt");

            return Boolean.TRUE.equals(verified) && verifiedAt != null &&
                    Instant.now().isBefore(verifiedAt.plus(Duration.ofMinutes(60)));
        }
        return false;
    }

    public static Map<String, Object> safeMapOf(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value != null ? value : "unknown");
        return map;
    }
}
