package com.talentboozt.s_backend.shared.tenant;

import com.talentboozt.s_backend.shared.identity.model.CredentialsModel;
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
    private static final String WORKSPACE_HEADER = "X-Workspace-Id";
    
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
        // Note: Automatic subdomain registration is virtual/dynamic in this codebase.
        // It relies on wildcard DNS records (e.g. *.talnova.io / *.edu.talnova.io) at the infrastructure
        // layer (Cloudflare/Netlify) to route all subdomains to the same backend. The backend then extracts
        // and resolves the tenant dynamically using the request's Host header.
        // (TEMPORARILY DISABLED: Netlify free plan does not support wildcard operators)
        /*
        if (tenantId == null) {
            tenantId = extractTenantFromSubdomain(request);
        }
        */
        // 4. Workspace isolation (EDU multi-tenancy)
        String workspaceId = request.getHeader(WORKSPACE_HEADER);
        context.setWorkspaceId(workspaceId);
        
        context.setTenantId(tenantId);
        context.setOrganizationId(organizationId);
        context.setResolved(tenantId != null || organizationId != null || workspaceId != null);
        
        return context;
    }
    
    /**
     * Extracts the virtual tenant subdomain from the Host header of the request.
     * This enables dynamic wildcard subdomain mapping without requiring explicit API calls to a DNS provider.
     * (TEMPORARILY DISABLED: Netlify free plan does not support wildcard operators)
     */
    /*
    private String extractTenantFromSubdomain(HttpServletRequest request) {
        String host = request.getHeader("Host");
        if (host != null && host.contains(".")) {
            String subdomain = host.split("\\.")[0];
            // Validate subdomain format - skip common/system subdomains like www or api
            if (subdomain != null && !subdomain.isEmpty() && 
                !subdomain.equals("www") && !subdomain.equals("api")) {
                return subdomain;
            }
        }
        return null;
    }
    */
}
