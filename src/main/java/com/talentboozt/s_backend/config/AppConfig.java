package com.talentboozt.s_backend.config;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // Configure connection pool to prevent resource leaks
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(200); // Maximum total connections
        connectionManager.setDefaultMaxPerRoute(50); // Maximum connections per route
        
        HttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .evictIdleConnections(Timeout.of(30, TimeUnit.SECONDS))
                .evictExpiredConnections()
                .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        factory.setConnectTimeout((int) Timeout.of(10, TimeUnit.SECONDS).toMilliseconds());
        factory.setConnectionRequestTimeout((int) Timeout.of(10, TimeUnit.SECONDS).toMilliseconds());
        factory.setReadTimeout((int) Timeout.of(30, TimeUnit.SECONDS).toMilliseconds());

        return builder
                .requestFactory(() -> factory)
                .build();
    }
}
