package com.talentboozt.s_backend.shared.tenant;

import com.talentboozt.s_backend.domains.auth.model.CredentialsModel;
import com.talentboozt.s_backend.shared.security.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Resolves tenant information from request headers, JWT tokens, or subdomain
 */
@Component
public class TenantResolver {
    
    private static final String TENANT_HEADER = "X-Tenant-Id";
    private static final String ORGANIZATION_HEADER = "X-Organization-Id";
    
    private final JwtService jwtService;
    
    public TenantResolver(JwtService jwtService) {
        this.jwtService = jwtService;
    }
    
    /**
     * Resolves tenant context from request
     * Priority: Header > JWT Token > Subdomain
     */
    public TenantContext resolve(HttpServletRequest request) {
        TenantContext context = TenantContext.getCurrent();
        
        // 1. Try header-based tenant resolution
        String tenantId = request.getHeader(TENANT_HEADER);
        String organizationId = request.getHeader(ORGANIZATION_HEADER);
        
        // 2. Try JWT token-based resolution
        if (tenantId == null || organizationId == null) {
            String token = jwtService.extractTokenFromHeaderOrCookie(request);
            if (token != null && jwtService.validateToken(token)) {
                try {
                    CredentialsModel user = jwtService.getUserFromToken(token);
                    if (user != null) {
                        context.setUserId(user.getEmployeeId());
                        
                        // Extract organization from user's organizations list
                        List<Map<String, String>> organizations = user.getOrganizations();
                        if (organizations != null && !organizations.isEmpty()) {
                            // Use first organization as default tenant
                            Map<String, String> org = organizations.get(0);
                            if (org.containsKey("companyId")) {
                                organizationId = org.get("companyId");
                            }
                            if (org.containsKey("tenantId")) {
                                tenantId = org.get("tenantId");
                            }
                        }
                    }
                } catch (Exception e) {
                    // Log but don't fail - allow anonymous requests
                }
            }
        }
        
        // 3. Try subdomain-based resolution (if needed)
        if (tenantId == null) {
            tenantId = extractTenantFromSubdomain(request);
        }
        
        context.setTenantId(tenantId);
        context.setOrganizationId(organizationId);
        context.setResolved(tenantId != null || organizationId != null);
        
        return context;
    }
    
    private String extractTenantFromSubdomain(HttpServletRequest request) {
        String host = request.getHeader("Host");
        if (host != null && host.contains(".")) {
            String subdomain = host.split("\\.")[0];
            // Validate subdomain format if needed
            if (subdomain != null && !subdomain.isEmpty() && 
                !subdomain.equals("www") && !subdomain.equals("api")) {
                return subdomain;
            }
        }
        return null;
    }
}
