package com.talentboozt.s_backend.shared.monitoring;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class MonitoringInterceptor implements HandlerInterceptor {
    private final MetricsService metricsService;
    private static final String START_TIME_ATTR = "startTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME_ATTR, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Long startTime = (Long) request.getAttribute(START_TIME_ATTR);
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            String path = request.getRequestURI();
            String method = request.getMethod();
            int status = response.getStatus();
            
            metricsService.incrementCounter("api.requests", "path", path, "method", method, "status", String.valueOf(status));
            // recordTime is for blocking, but we already have the delta
            // metricsService.recordValue("api.duration", (double) duration, "path", path);
        }
        
        if (ex != null) {
            metricsService.incrementCounter("api.errors", "exception", ex.getClass().getSimpleName());
        }
    }
}
