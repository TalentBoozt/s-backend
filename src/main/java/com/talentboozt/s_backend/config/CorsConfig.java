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
        registry.addMapping("/**")
                .allowedOrigins(configUtility.getProperty("ALLOWED_ORIGIN_1"),
                        configUtility.getProperty("ALLOWED_ORIGIN_2"),
                        configUtility.getProperty("ALLOWED_ORIGIN_3"),
                        configUtility.getProperty("ALLOWED_ORIGIN_4"),
                        configUtility.getProperty("ALLOWED_ORIGIN_5"),
                        configUtility.getProperty("ALLOWED_ORIGIN_6"),
                        configUtility.getProperty("ALLOWED_ORIGIN_7"),
                        configUtility.getProperty("ALLOWED_ORIGIN_8"),
                        configUtility.getProperty("ALLOWED_ORIGIN_9"),
                        configUtility.getProperty("ALLOWED_ORIGIN_10"))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Stripe-Signature", "X-Demo-Mode")
                .allowCredentials(true);
    }
}
