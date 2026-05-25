package com.talentboozt.s_backend.domains.edu.seo.analytics;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;

/**
 * AI Crawler Interceptor Filter.
 * Listens to all incoming servlet traffic, classifies search bot user-agents in real time,
 * and records crawler behavior in MongoDB to observe generative search visibility.
 */
@Component
public class AICrawlerTracker implements Filter {

    @Autowired
    private BotClassificationEngine classificationEngine;

    @Autowired
    private CrawlAnalyticsService analyticsService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        long startTime = System.currentTimeMillis();
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String userAgent = httpRequest.getHeader("User-Agent");
        String botName = classificationEngine.classifyUserAgent(userAgent);
        
        chain.doFilter(request, response);
        
        if (botName != null) {
            long duration = System.currentTimeMillis() - startTime;
            String requestUri = httpRequest.getRequestURI();
            String queryStr = httpRequest.getQueryString();
            if (queryStr != null) {
                requestUri += "?" + queryStr;
            }
            String ip = httpRequest.getHeader("X-Forwarded-For");
            if (ip == null || ip.isBlank()) {
                ip = request.getRemoteAddr();
            }
            
            String delivery = "Pre-rendered SEO Page/Metadata JSON";
            int status = httpResponse.getStatus();
            
            // Record bot log asynchronously to prevent response delay
            final String finalBotName = botName;
            final String finalUri = requestUri;
            final String finalIp = ip;
            new Thread(() -> {
                try {
                    analyticsService.recordVisit(finalBotName, finalUri, userAgent, finalIp, duration, status, delivery);
                } catch (Exception e) {
                    System.err.println("Failed to log AI Crawler visit: " + e.getMessage());
                }
            }).start();
        }
    }

    @Override
    public void destroy() {}
}
