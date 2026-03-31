package com.talentboozt.s_backend.shared.security.cfg;

import com.talentboozt.s_backend.domains.auth.model.CredentialsModel;
import com.talentboozt.s_backend.shared.security.annotations.AuthenticatedUser;
import com.talentboozt.s_backend.shared.security.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class AuthenticatedUserResolver implements HandlerMethodArgumentResolver {

    private final JwtService jwtService;

    public AuthenticatedUserResolver(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthenticatedUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String token = jwtService.extractTokenFromHeaderOrCookie(request);

        if (token != null && jwtService.validateToken(token)) {
            CredentialsModel user = jwtService.getUserFromToken(token);
            if (user != null) {
                String userId = user.getEmployeeId();
                if (userId != null && !userId.isEmpty() && !"n/a".equals(userId)) {
                    return userId;
                }
            }
        }

        // Check if the parameter is required. Typically, we expect authentication because of @PreAuthorize,
        // but this resolver should be robust.
        throw new RuntimeException("Authentication Required: No valid JWT found in cookies or headers for " + 
            parameter.getParameterName() + ". Please check your login status.");
    }
}
