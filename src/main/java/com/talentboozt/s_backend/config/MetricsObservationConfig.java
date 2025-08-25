package com.talentboozt.s_backend.config;

import io.micrometer.common.KeyValues;
import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.observation.DefaultServerRequestObservationConvention;
import org.springframework.http.server.observation.ServerRequestObservationContext;
import org.springframework.http.server.observation.ServerRequestObservationConvention;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Optional;

@Configuration
public class MetricsObservationConfig {

    @Bean
    public ServerRequestObservationConvention customServerRequestObservationConvention() {
        return new DefaultServerRequestObservationConvention() {
            @NotNull
            @Override
            public String getName() {
                return "http.server.requests";
            }

            @NotNull
            @Override
            public KeyValues getLowCardinalityKeyValues(@NotNull ServerRequestObservationContext context) {
                HttpServletRequest request = context.getCarrier();

                String uri = Optional.ofNullable((String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE))
                        .orElse("UNKNOWN");

                String normalizedUri = uri.replaceAll("/\\d+", "/{id}");

                return KeyValues.of(
                        "method", request.getMethod(),
                        "uri", normalizedUri,
                        "status", String.valueOf(context.getResponse().getStatus())
                );
            }
        };
    }
}
