package com.talentboozt.s_backend.config;

import com.talentboozt.s_backend.shared.utils.ConfigUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Autowired
    private ConfigUtility configUtility;

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        String origins = configUtility.getProperty("ALLOWED_ORIGINS");
        String methods = configUtility.getProperty("ALLOWED_METHODS");
        String headers = configUtility.getProperty("ALLOWED_HEADERS");
        String exposedHeaders = configUtility.getProperty("EXPOSED_HEADERS");

        registry.addMapping("/**")
                .allowedOrigins(origins != null ? origins.split(",") : new String[]{"*"})
                .allowedMethods(methods != null ? methods.split(",") : new String[]{"GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"})
                .allowedHeaders(headers != null ? headers.split(",") : new String[]{"*"})
                .exposedHeaders(exposedHeaders != null ? exposedHeaders.split(",") : new String[]{"X-XSRF-TOKEN", "x-user-id"})
                .allowCredentials(true);
    }
}
