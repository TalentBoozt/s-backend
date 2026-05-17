package com.talentboozt.s_backend.shared.monitoring;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class MetricsService {
    private final MeterRegistry meterRegistry;

    public void incrementCounter(String name, String... tags) {
        meterRegistry.counter(name, tags).increment();
    }

    public void recordValue(String name, double value, String... tags) {
        meterRegistry.gauge(name, value);
    }

    public <T> T recordTime(String name, Supplier<T> action, String... tags) {
        Timer timer = meterRegistry.timer(name, tags);
        return timer.record(action);
    }
}
