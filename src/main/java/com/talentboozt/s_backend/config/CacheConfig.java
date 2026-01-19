package com.talentboozt.s_backend.config;

import com.github.benmanes.caffeine.cache.Caffeine;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Centralized cache configuration using Caffeine
 * Provides high-performance in-memory caching for frequently accessed data
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        List<CaffeineCache> caches = List.of(
            new CaffeineCache("userCredentials",
            Objects.requireNonNull(
                Caffeine.newBuilder()
                    .expireAfterWrite(10, TimeUnit.MINUTES)
                    .maximumSize(10_000)
                    .recordStats()
                    .build())),

            new CaffeineCache("organizations",
            Objects.requireNonNull(
                Caffeine.newBuilder()
                    .expireAfterWrite(1, TimeUnit.HOURS)
                    .maximumSize(5_000)
                    .recordStats()
                    .build())),

            new CaffeineCache("jwtTokens",
            Objects.requireNonNull(
                Caffeine.newBuilder()
                    .expireAfterWrite(5, TimeUnit.MINUTES)
                    .maximumSize(50_000)
                    .recordStats()
                    .build())),

            new CaffeineCache("courses",
            Objects.requireNonNull(
                Caffeine.newBuilder()
                    .expireAfterWrite(30, TimeUnit.MINUTES)
                    .maximumSize(20_000)
                    .recordStats()
                    .build())),

            new CaffeineCache("jobListings",
            Objects.requireNonNull(
                Caffeine.newBuilder()
                    .expireAfterWrite(15, TimeUnit.MINUTES)
                    .maximumSize(15_000)
                    .recordStats()
                    .build())),

            new CaffeineCache("configurations",
            Objects.requireNonNull(
                Caffeine.newBuilder()
                    .expireAfterWrite(1, TimeUnit.HOURS)
                    .maximumSize(1_000)
                    .recordStats()
                    .build()))
        );

        cacheManager.setCaches(caches);
        return cacheManager;
    }
}
