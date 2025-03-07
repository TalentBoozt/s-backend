package com.talentboozt.s_backend.Service._private;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserActivityMetrics {

    private final MeterRegistry meterRegistry;

    public UserActivityMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void incrementActiveUsers() {
        meterRegistry.counter("user.active.count").increment();
    }

    public void decrementActiveUsers() {
        meterRegistry.counter("user.active.count").increment(-1);
    }

    public void recordSessionDuration(double durationInSeconds) {
        meterRegistry.timer("user.session.duration").record((long) durationInSeconds, TimeUnit.SECONDS);
    }
}

