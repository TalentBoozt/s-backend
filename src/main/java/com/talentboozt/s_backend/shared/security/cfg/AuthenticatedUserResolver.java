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
                return user.getEmployeeId(); // This is the user ID
            }
        }

        // Return null or throw exception if user is not authenticated but @AuthenticatedUser is present
        // Since we have @PreAuthorize on controllers, we expect the token to be valid here.
        throw new RuntimeException("Unauthorized: Valid JWT required for @AuthenticatedUser access.");
    }
}
