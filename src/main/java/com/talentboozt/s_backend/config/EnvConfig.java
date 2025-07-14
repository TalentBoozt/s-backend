package com.talentboozt.s_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.properties")
public class EnvConfig {

    @Value("${env.connection}")
    public String ENV_CONNECTION;
}
