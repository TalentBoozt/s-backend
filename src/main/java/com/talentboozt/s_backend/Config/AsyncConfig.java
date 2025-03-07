package com.talentboozt.s_backend.Config;

import com.talentboozt.s_backend.Shared.ContextPropagatingTaskDecorator;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor(MeterRegistry meterRegistry) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("AsyncTask-");
        executor.setTaskDecorator(new ContextPropagatingTaskDecorator());
        executor.initialize();

        ExecutorServiceMetrics.monitor(
                meterRegistry,
                executor.getThreadPoolExecutor(),
                "taskExecutor",
                Tags.of("executor.name", "AsyncTaskExecutor")
        );

        return executor;
    }
}
