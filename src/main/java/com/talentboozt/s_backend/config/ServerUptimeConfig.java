package com.talentboozt.s_backend.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Configuration;

import java.lang.management.ManagementFactory;
import java.time.Duration;

@Configuration
public class ServerUptimeConfig {

    public ServerUptimeConfig(MeterRegistry meterRegistry) {
        long startTime = ManagementFactory.getRuntimeMXBean().getStartTime();
        meterRegistry.gauge("server.uptime", Duration.ofMillis(System.currentTimeMillis() - startTime).toSeconds());
    }
}
