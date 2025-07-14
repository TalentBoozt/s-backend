package com.talentboozt.s_backend.shared.security.cfg;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;

@Component
public class UptimeHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        return Health.up().withDetail("uptime", uptime + "ms").build();
    }
}

