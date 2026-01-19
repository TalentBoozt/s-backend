package com.talentboozt.s_backend.shared.tenant;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter to resolve and set tenant context for each request
 * Must run early in the filter chain to ensure tenant context is available
 */
@Component
@Order(1)
public class TenantContextFilter extends OncePerRequestFilter {
    
    private final TenantResolver tenantResolver;
    
    public TenantContextFilter(TenantResolver tenantResolver) {
        this.tenantResolver = tenantResolver;
    }
    
    @Override
    protected void initFilterBean() throws ServletException {
        // Ensure filter is properly initialized
        super.initFilterBean();
    }
    
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, 
                                   @NonNull HttpServletResponse response, 
                                   @NonNull FilterChain filterChain) 
            throws ServletException, IOException {
        
        try {
            // Resolve tenant context from request
            tenantResolver.resolve(request);
            
            // Continue filter chain
            filterChain.doFilter(request, response);
        } finally {
            // Always clear tenant context after request to prevent leaks
            TenantContext.clear();
        }
    }
    
    @Override
    protected boolean shouldNotFilter(@NonNull jakarta.servlet.http.HttpServletRequest request) {
        // Skip tenant resolution for actuator endpoints
        String path = request.getRequestURI();
        return path.startsWith("/actuator/") || 
               path.startsWith("/public/") ||
               path.startsWith("/stripe/webhook");
    }
}
