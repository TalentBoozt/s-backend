package com.talentboozt.s_backend.shared.security.cfg;

import com.talentboozt.s_backend.domains.edu.model.EApiKey;
import com.talentboozt.s_backend.domains.edu.service.EduApiKeyService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private final EduApiKeyService apiKeyService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String apiKey = request.getHeader("X-API-KEY");
        
        if (apiKey != null && !apiKey.isEmpty()) {
            EApiKey keyRecord = apiKeyService.validateKey(apiKey);
            
            if (keyRecord != null && keyRecord.isActive()) {
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        keyRecord.getOwnerId(),
                        null,
                        keyRecord.getScopes().stream()
                                .map(s -> new SimpleGrantedAuthority("SCOPE_" + s))
                                .collect(Collectors.toList())
                );
                
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
