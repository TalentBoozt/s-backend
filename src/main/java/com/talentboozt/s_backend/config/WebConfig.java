package com.talentboozt.s_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import org.springframework.data.web.config.EnableSpringDataWebSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import com.talentboozt.s_backend.shared.security.interceptor.RbacInterceptor;
import com.talentboozt.s_backend.shared.security.cfg.AuthenticatedUserResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private RbacInterceptor rbacInterceptor;

    @Autowired
    private AuthenticatedUserResolver authenticatedUserResolver;

    @Override
    public void extendMessageConverters(@NonNull List<HttpMessageConverter<?>> converters) {
        converters.add(new AllEncompassingFormHttpMessageConverter());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rbacInterceptor)
                .addPathPatterns("/api/edu/**"); // Default apply to edu paths
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authenticatedUserResolver);
    }
}