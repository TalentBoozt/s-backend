package com.talentboozt.s_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        String externalPath = Paths.get("docs/").toAbsolutePath().toUri().toString();

        registry.addResourceHandler("/docs/**")
                .addResourceLocations(externalPath)
                .setCachePeriod(3600);
    }
}

